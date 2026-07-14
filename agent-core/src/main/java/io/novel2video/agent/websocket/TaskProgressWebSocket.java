package io.novel2video.agent.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务进度 WebSocket Handler
 *
 * 向前端推送任务执行进度
 */
@Slf4j
@Component
public class TaskProgressWebSocket implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储 taskId -> WebSocketSession 映射
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String taskId = getTaskId(session);
        if (taskId != null) {
            sessions.put(taskId, session);
            log.info("WebSocket connected for task: {}", taskId);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 不需要处理客户端消息
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String taskId = getTaskId(session);
        if (taskId != null) {
            sessions.remove(taskId);
            log.info("WebSocket disconnected for task: {}", taskId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 推送进度更新
     */
    public void sendProgress(String taskId, int progress, String step, String message) {
        WebSocketSession session = sessions.get(taskId);
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            Map<String, Object> data = Map.of(
                    "type", "progress",
                    "taskId", taskId,
                    "progress", progress,
                    "step", step,
                    "message", message,
                    "timestamp", System.currentTimeMillis()
            );

            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send progress update", e);
        }
    }

    /**
     * 推送任务完成
     */
    public void sendCompleted(String taskId, Object result) {
        WebSocketSession session = sessions.get(taskId);
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            Map<String, Object> data = Map.of(
                    "type", "completed",
                    "taskId", taskId,
                    "result", result,
                    "timestamp", System.currentTimeMillis()
            );

            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send completion message", e);
        }
    }

    /**
     * 推送错误
     */
    public void sendError(String taskId, String errorMessage) {
        WebSocketSession session = sessions.get(taskId);
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            Map<String, Object> data = Map.of(
                    "type", "error",
                    "taskId", taskId,
                    "error", errorMessage,
                    "timestamp", System.currentTimeMillis()
            );

            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("Failed to send error message", e);
        }
    }

    private String getTaskId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("taskId=")) {
            return query.split("taskId=")[1].split("&")[0];
        }
        return null;
    }
}
