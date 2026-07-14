package io.novel2video.agent.worker;

import io.novel2video.agent.ai.AiProvider;
import io.novel2video.agent.ai.AiProviderFactory;
import io.novel2video.agent.engine.SkillExecutor;
import io.novel2video.agent.entity.Task;
import io.novel2video.agent.entity.TaskStep;
import io.novel2video.agent.queue.TaskQueueService;
import io.novel2video.agent.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;
import java.util.Map;

/**
 * 任务执行器
 *
 * 从 Redis Streams 消费任务并执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutor {

    private final TaskQueueService queueService;
    private final TaskService taskService;
    private final AiProviderFactory providerFactory;
    private final SkillExecutor skillExecutor;

    /**
     * 启动任务消费者（异步执行）
     */
    @Async
    public void startConsuming() {
        log.info("Task executor started, waiting for tasks...");

        while (true) {
            try {
                List<StreamEntry> entries = queueService.consumeTasks(10);

                for (StreamEntry entry : entries) {
                    try {
                        processTask(entry);
                        queueService.acknowledgeTask(entry.getID());
                    } catch (Exception e) {
                        log.error("Failed to process task entry: {}", entry.getID(), e);
                        // 任务失败不确认，让其他 worker 接管
                    }
                }
            } catch (Exception e) {
                log.error("Error in task consumer loop", e);
                try {
                    Thread.sleep(5000); // 出错后等待 5 秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void processTask(StreamEntry entry) {
        Map<String, String> fields = entry.getFields();
        String taskId = fields.get("taskId");

        log.info("Processing task: {}", taskId);

        Task task = taskService.findByTaskId(taskId).orElse(null);
        if (task == null) {
            log.warn("Task not found: {}", taskId);
            return;
        }

        // 更新任务状态为处理中
        taskService.updateTaskStatus(taskId, "processing");

        try {
            // 获取任务步骤
            List<TaskStep> steps = taskService.findStepsByTaskId(taskId);

            // 依次执行每个步骤
            for (TaskStep step : steps) {
                executeStep(task, step);
            }

            // 所有步骤完成
            taskService.updateTaskStatus(taskId, "completed");
            log.info("Task completed: {}", taskId);

        } catch (Exception e) {
            log.error("Task execution failed: {}", taskId, e);
            taskService.updateTaskStatus(taskId, "failed");

            Task finalTask = taskService.findByTaskId(taskId).orElse(task);
            finalTask.setErrorMessage(e.getMessage());
            taskService.updateTaskStatus(taskId, "failed");
        }
    }

    private void executeStep(Task task, TaskStep step) {
        log.info("Executing step: {} for task {}", step.getStepName(), task.getTaskId());

        // 更新步骤状态为运行中
        taskService.updateStepStatus(step.getId(), "running");
        taskService.updateProgress(task.getTaskId(), calculateProgress(task, step), step.getStepName());

        try {
            // 使用 SkillExecutor 执行
            Map<String, Object> input = step.getInputData() != null ?
                    (Map<String, Object>) step.getInputData() : Map.of();

            Object result = skillExecutor.execute(step.getSkillId(), input);

            // 保存结果
            step.setOutputData(result);
            taskService.updateStepStatus(step.getId(), "completed");

            log.info("Step completed: {} with result type: {}",
                    step.getStepName(), result != null ? result.getClass().getSimpleName() : "null");

        } catch (Exception e) {
            log.error("Step failed: {} for task {}", step.getStepName(), task.getTaskId(), e);
            step.setErrorMessage(e.getMessage());
            taskService.updateStepStatus(step.getId(), "failed");
            throw e;
        }
    }

    private int calculateProgress(Task task, TaskStep currentStep) {
        if (task.getTotalSteps() == null || task.getTotalSteps() == 0) return 0;

        int completedSteps = task.getCompletedSteps() != null ? task.getCompletedSteps() : 0;
        int stepProgress = currentStep.getProgress() != null ? currentStep.getProgress() : 0;

        // 计算总进度 = (已完成步骤数 / 总步骤数) * 100
        return (int) (((completedSteps + stepProgress / 100.0) / task.getTotalSteps()) * 100);
    }
}
