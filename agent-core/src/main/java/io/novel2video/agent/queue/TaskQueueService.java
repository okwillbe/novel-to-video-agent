package io.novel2video.agent.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.novel2video.agent.entity.Task;
import io.novel2video.agent.entity.TaskStep;
import io.novel2video.agent.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.resps.StreamEntry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Task Queue Service using Redis Streams
 *
 * Replaces waoowaoo's BullMQ (TypeScript) with Redis Streams (Java).
 * Provides similar semantics: task publishing, consumption, and health checking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskQueueService {

    private final JedisPool jedisPool;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    @Value("${task.queue.stream-key:novel2video:tasks}")
    private String streamKey;

    @Value("${task.queue.consumer-group:workers}")
    private String consumerGroup;

    @Value("${task.queue.poll-timeout-ms:5000}")
    private int pollTimeoutMs;

    @Value("${task.queue.claim-interval-ms:60000}")
    private long claimIntervalMs;

    private static final String CONSUMER_NAME = "worker-" + UUID.randomUUID().toString().substring(0, 8);

    /**
     * Publish a task to the Redis Stream for worker pickup
     */
    public void publishTask(Task task) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> message = new HashMap<>();
            message.put("taskId", task.getTaskId());
            message.put("taskType", task.getTaskType());
            message.put("userId", task.getUserId());
            message.put("priority", String.valueOf(task.getPriority()));
            message.put("createdAt", LocalDateTime.now().toString());

            StreamEntryID id = jedis.xadd(streamKey, StreamEntryID.NEW_ENTRY, message);
            log.info("Published task {} to stream, entryId={}", task.getTaskId(), id);

            // Ensure consumer group exists
            try {
                jedis.xgroupCreate(streamKey, consumerGroup, StreamEntryID.LAST_ENTRY, true);
            } catch (Exception e) {
                // Group already exists, ignore
            }
        }
    }

    /**
     * Consume tasks from the Redis Stream
     */
    public List<StreamEntry> consumeTasks(int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, StreamEntryID> streams = Map.of(streamKey, StreamEntryID.UNRECEIVED_ENTRY);
            List<Map.Entry<String, List<StreamEntry>>> results = jedis.xreadgroup(
                    consumerGroup,
                    CONSUMER_NAME,
                    XReadParams.xReadParams().count(count).block(pollTimeoutMs),
                    streams
            );

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }

            return results.get(0).getValue();
        }
    }

    /**
     * Acknowledge a processed task
     */
    public void acknowledgeTask(String entryId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.xack(streamKey, consumerGroup, new StreamEntryID(entryId));
        }
    }

    /**
     * Claim stuck tasks that exceeded the processing timeout
     */
    @Scheduled(fixedDelayString = "${task.queue.claim-interval-ms:60000}")
    public void claimStuckTasks() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Find pending entries that are older than the claim interval
            var pendingInfo = jedis.xpending(streamKey, consumerGroup);
            if (pendingInfo == null || pendingInfo.size() == 0) {
                return;
            }

            // Get pending entries details
            var pendingEntries = jedis.xpending(streamKey, consumerGroup, null, null, 10);
            for (var entry : pendingEntries) {
                // If a message has been idle for too long, claim it
                if (entry.getIdleTime() > claimIntervalMs * 2) {
                    try {
                        jedis.xclaim(streamKey, consumerGroup, CONSUMER_NAME,
                                claimIntervalMs, new StreamEntryID(entry.getID()));
                        log.warn("Claimed stuck task entry: {}", entry.getID());
                    } catch (Exception e) {
                        log.error("Failed to claim stuck task: {}", entry.getID(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking stuck tasks", e);
        }
    }

    /**
     * Health check - verify Redis Stream is operational
     */
    public boolean isHealthy() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ping().equals("PONG");
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return false;
        }
    }
}
