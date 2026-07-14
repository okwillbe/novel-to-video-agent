package io.novel2video.agent.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.novel2video.agent.entity.Skill;
import io.novel2video.agent.entity.Task;
import io.novel2video.agent.entity.TaskStep;
import io.novel2video.agent.service.SkillService;
import io.novel2video.agent.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Orchestrator Agent - Core decision engine using ReAct pattern
 *
 * Translates agentscope-java's ReActAgent pattern into a task orchestration system.
 * The agent receives user intent, selects appropriate Skills from MySQL repository,
 * plans execution steps, and delegates to worker processes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrchestratorAgent {

    private final SkillService skillService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    /**
     * Analyze user intent and create an execution plan
     *
     * @param userId    User ID
     * @param sessionId Session ID
     * @param input     User input (natural language description)
     * @return Created Task with planned steps
     */
    public Task planAndCreateTask(String userId, String sessionId, Map<String, Object> input) {
        // 1. Understand intent
        String taskType = determineTaskType(input);
        log.info("Planning task for user={}, type={}", userId, taskType);

        // 2. Create task record
        Task task = new Task()
                .setTaskId(generateTaskId())
                .setUserId(userId)
                .setSessionId(sessionId)
                .setTaskType(taskType)
                .setStatus("pending")
                .setPriority(5)
                .setInputData(input)
                .setProgress(0)
                .setRetryCount(0)
                .setMaxRetries(3);
        taskService.createTask(task);

        // 3. Select appropriate skills and create steps
        List<TaskStep> steps = planSteps(task, input);
        taskService.createSteps(task.getTaskId(), steps);

        // 4. Publish task to Redis Streams for worker pickup
        // (handled by TaskQueueService after task creation)

        return task;
    }

    /**
     * Determine task type from user input
     */
    private String determineTaskType(Map<String, Object> input) {
        String content = (String) input.getOrDefault("content", "");
        String style = (String) input.getOrDefault("style", "");

        // Simple heuristic-based classification
        // In production, this would use LLM for intent classification
        if (content.length() > 500) {
            return "novel_to_video";
        } else if (input.containsKey("characterImages") || input.containsKey("character_images")) {
            return "novel_to_video";
        } else if (style != null && !style.isEmpty()) {
            return "novel_to_video";
        }
        return "novel_to_video";
    }

    /**
     * Plan execution steps by selecting appropriate Skills
     *
     * This is the core planning logic that maps user intent to a sequence
     * of Skills, similar to how agentscope-java's ReActAgent reasons about
     * which tools to use.
     */
    private List<TaskStep> planSteps(Task task, Map<String, Object> input) {
        List<TaskStep> steps = new ArrayList<>();
        String taskType = task.getTaskType();

        if ("novel_to_video".equals(taskType)) {
            steps.add(createStep("novel_analysis", "novel-analysis_v1", "分析小说内容，提取角色和场景"));
            steps.add(createStep("character_extraction", "character-extraction_v1", "提取角色信息及视觉特征"));
            steps.add(createStep("scene_extraction", "scene-extraction_v1", "识别和提取场景"));
            steps.add(createStep("script_generation", "script-generation_v1", "生成剧本"));
            steps.add(createStep("storyboard_design", "storyboard-design_v1", "设计分镜方案"));
            steps.add(createStep("image_generation", "image-generation_v1", "生成角色和场景图片"));
            steps.add(createStep("consistency_check", "consistency-check_v1", "校验视觉一致性"));
            steps.add(createStep("video_synthesis", "video-synthesis_v1", "合成视频"));
            steps.add(createStep("voice_generation", "voice-generation_v1", "生成配音"));
            steps.add(createStep("final_composition", "video-synthesis_v1", "最终合成（视频+配音）"));
        }

        log.info("Planned {} steps for task {}", steps.size(), task.getTaskId());
        return steps;
    }

    private TaskStep createStep(String stepName, String skillId, String description) {
        TaskStep step = new TaskStep();
        step.setStepName(stepName);
        step.setSkillId(skillId);
        step.setInputData(Map.of("description", description));
        step.setProgress(0);
        return step;
    }

    /**
     * Evaluate task result and determine if retry or adjustment is needed
     */
    public boolean evaluateStepResult(TaskStep step) {
        if ("failed".equals(step.getStatus())) {
            log.warn("Step {} failed: {}", step.getStepName(), step.getErrorMessage());
            return false;
        }
        if (step.getOutputData() == null) {
            log.warn("Step {} produced no output", step.getStepName());
            return false;
        }
        return true;
    }

    /**
     * Dynamic replanning when a step fails
     */
    public List<TaskStep> replanFromFailure(Task task, TaskStep failedStep) {
        log.info("Replanning from failed step: {} in task {}", failedStep.getStepName(), task.getTaskId());

        // Simple retry strategy: try with alternative skill or parameters
        // In production, this would use LLM to decide the best alternative
        List<TaskStep> alternativeSteps = new ArrayList<>();

        String failedSkillId = failedStep.getSkillId();
        // Could look for alternative skills in the same category
        List<Skill> alternatives = skillService.findByCategory(
                getSkillCategory(failedSkillId));

        if (!alternatives.isEmpty()) {
            Skill altSkill = alternatives.stream()
                    .filter(s -> !s.getSkillId().equals(failedSkillId))
                    .findFirst()
                    .orElse(null);

            if (altSkill != null) {
                TaskStep retryStep = createStep(
                        failedStep.getStepName() + "_retry",
                        altSkill.getSkillId(),
                        "Retry with alternative: " + altSkill.getName());
                retryStep.setInputData(failedStep.getInputData());
                alternativeSteps.add(retryStep);
            }
        }

        return alternativeSteps;
    }

    private String getSkillCategory(String skillId) {
        if (skillId.contains("analysis") || skillId.contains("extraction")) {
            return "analysis";
        } else if (skillId.contains("generation") || skillId.contains("design")) {
            return "generation";
        } else if (skillId.contains("synthesis") || skillId.contains("voice")) {
            return "synthesis";
        } else {
            return "postprocess";
        }
    }

    private String generateTaskId() {
        return "task_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
