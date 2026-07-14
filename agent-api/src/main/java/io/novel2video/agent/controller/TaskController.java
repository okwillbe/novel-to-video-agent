package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.Task;
import io.novel2video.agent.entity.TaskStep;
import io.novel2video.agent.entity.User;
import io.novel2video.agent.engine.OrchestratorAgent;
import io.novel2video.agent.queue.TaskQueueService;
import io.novel2video.agent.service.TaskService;
import io.novel2video.agent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task API Controller
 *
 * 单用户模式：所有任务都属于默认用户
 *
 * REST API for task creation and management.
 */
@Tag(name = "Task API", description = "Video generation task management")
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final OrchestratorAgent orchestratorAgent;
    private final TaskService taskService;
    private final TaskQueueService taskQueueService;
    private final UserService userService;

    @Operation(summary = "Create a video generation task")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<TaskCreatedResponse>> generate(
            @Valid @RequestBody GenerateRequest request) {

        // 单用户模式：使用默认用户
        User user = userService.getOrCreateDefaultUser();

        // Build input from request
        Map<String, Object> input = new HashMap<>();
        input.put("content", request.getContent());
        input.put("style", request.getStyle());
        input.put("duration", request.getDuration());
        if (request.getOptions() != null) {
            input.putAll(request.getOptions());
        }

        // Create task via Orchestrator Agent
        Task task = orchestratorAgent.planAndCreateTask(
                user.getUserId(),
                null,
                input
        );

        // Publish to queue for worker processing
        taskQueueService.publishTask(task);

        log.info("Created task {} for user {}", task.getTaskId(), user.getUserId());

        return ResponseEntity.ok(ApiResponse.success(
                new TaskCreatedResponse(task.getTaskId(), "processing", estimateDuration(request.getDuration()))
        ));
    }

    @Operation(summary = "Get task status and progress")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskStatusResponse>> getTaskStatus(@PathVariable String taskId) {
        Task task = taskService.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        var steps = taskService.findStepsByTaskId(taskId);

        List<TaskStatusResponse.StepInfo> stepInfos = steps.stream()
                .map(s -> new TaskStatusResponse.StepInfo(
                        s.getStepName(),
                        s.getStatus(),
                        s.getProgress()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                new TaskStatusResponse(
                        task.getTaskId(),
                        task.getStatus(),
                        task.getProgress(),
                        task.getCurrentStep(),
                        stepInfos
                )
        ));
    }

    @Operation(summary = "List tasks")
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskSummaryResponse>>> listTasks(
            @RequestParam(defaultValue = "20") int limit) {

        // 单用户模式：获取默认用户的任务
        User user = userService.getOrCreateDefaultUser();

        List<Task> tasks = taskService.findByUserId(user.getUserId())
                .stream()
                .limit(limit)
                .toList();

        List<TaskSummaryResponse> summaries = tasks.stream()
                .map(t -> new TaskSummaryResponse(
                        t.getTaskId(),
                        t.getTaskType(),
                        t.getStatus(),
                        t.getProgress(),
                        t.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(summaries));
    }

    @Operation(summary = "Cancel a task")
    @PostMapping("/tasks/{taskId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTask(@PathVariable String taskId) {
        taskService.updateTaskStatus(taskId, "cancelled");
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private int estimateDuration(Integer requestedDuration) {
        int duration = requestedDuration != null ? requestedDuration : 180;
        return duration * 5;
    }
}
