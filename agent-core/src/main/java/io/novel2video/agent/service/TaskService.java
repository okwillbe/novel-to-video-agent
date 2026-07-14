package io.novel2video.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.novel2video.agent.entity.Task;
import io.novel2video.agent.entity.TaskStep;
import io.novel2video.agent.mapper.TaskMapper;
import io.novel2video.agent.mapper.TaskStepMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Task Service - Task lifecycle management
 *
 * Inspired by waoowaoo's task queue system but using Redis Streams
 * for Java instead of BullMQ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskStepMapper taskStepMapper;

    @Transactional
    public Task createTask(Task task) {
        taskMapper.insert(task);
        log.info("Created task: {} type={}", task.getTaskId(), task.getTaskType());
        return task;
    }

    public Optional<Task> findByTaskId(String taskId) {
        return Optional.ofNullable(taskMapper.selectOne(
                new LambdaQueryWrapper<Task>().eq(Task::getTaskId, taskId)));
    }

    public List<Task> findByUserId(String userId) {
        return taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getUserId, userId)
                        .orderByDesc(Task::getCreatedAt));
    }

    public List<Task> findPendingTasks() {
        return taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getStatus, "pending")
                        .orderByAsc(Task::getPriority)
                        .orderByAsc(Task::getCreatedAt));
    }

    @Transactional
    public Task updateTaskStatus(String taskId, String status) {
        Task task = findByTaskId(taskId).orElseThrow();
        task.setStatus(status);

        if ("processing".equals(status) && task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        }
        if ("completed".equals(status) || "failed".equals(status) || "cancelled".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        }

        taskMapper.updateById(task);
        log.info("Task {} status -> {}", taskId, status);
        return task;
    }

    @Transactional
    public void updateProgress(String taskId, int progress, String currentStep) {
        Task task = findByTaskId(taskId).orElseThrow();
        task.setProgress(progress);
        task.setCurrentStep(currentStep);
        taskMapper.updateById(task);
    }

    // ---- Step Management ----

    @Transactional
    public void createSteps(String taskId, List<TaskStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setTaskId(taskId);
            steps.get(i).setStepIndex(i);
            steps.get(i).setStatus("pending");
            taskStepMapper.insert(steps.get(i));
        }
        Task task = findByTaskId(taskId).orElseThrow();
        task.setTotalSteps(steps.size());
        task.setCompletedSteps(0);
        taskMapper.updateById(task);
    }

    @Transactional
    public void updateStepStatus(Long stepId, String status) {
        TaskStep step = taskStepMapper.selectById(stepId);
        step.setStatus(status);

        if ("running".equals(status)) {
            step.setStartedAt(LocalDateTime.now());
        }
        if ("completed".equals(status) || "failed".equals(status) || "skipped".equals(status)) {
            step.setCompletedAt(LocalDateTime.now());
            if (step.getStartedAt() != null) {
                long ms = java.time.Duration.between(step.getStartedAt(), step.getCompletedAt()).toMillis();
                step.setExecutionTimeMs((int) ms);
            }

            if ("completed".equals(status)) {
                Task task = findByTaskId(step.getTaskId()).orElseThrow();
                task.setCompletedSteps(task.getCompletedSteps() + 1);
                if (task.getTotalSteps() > 0) {
                    task.setProgress((int) ((task.getCompletedSteps() * 100.0) / task.getTotalSteps()));
                }
                taskMapper.updateById(task);
            }
        }

        taskStepMapper.updateById(step);
    }

    public List<TaskStep> findStepsByTaskId(String taskId) {
        return taskStepMapper.selectList(
                new LambdaQueryWrapper<TaskStep>()
                        .eq(TaskStep::getTaskId, taskId)
                        .orderByAsc(TaskStep::getStepIndex));
    }
}
