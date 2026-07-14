package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Task Entity - Core task tracking
 *
 * Represents an agent execution task with full lifecycle tracking.
 */
@Data
@Accessors(chain = true)
@TableName("tasks")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Unique task identifier
     */
    private String taskId;

    /**
     * User who created the task
     */
    private String userId;

    /**
     * Associated agent session
     */
    private String sessionId;

    /**
     * Task type: novel_to_video, image_gen, voice_gen, etc.
     */
    private String taskType;

    /**
     * Task status: pending, processing, completed, failed, cancelled
     */
    private String status;

    /**
     * Priority 1-10, higher is more important
     */
    private Integer priority;

    /**
     * Input data (JSON)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object inputData;

    /**
     * Output data (JSON)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object outputData;

    /**
     * Error message if failed
     */
    private String errorMessage;

    /**
     * Overall progress 0-100
     */
    private Integer progress;

    /**
     * Current step name
     */
    private String currentStep;

    /**
     * Total number of steps
     */
    private Integer totalSteps;

    /**
     * Number of completed steps
     */
    private Integer completedSteps;

    /**
     * Number of retries
     */
    private Integer retryCount;

    /**
     * Maximum retries allowed
     */
    private Integer maxRetries;

    /**
     * When the task started
     */
    private LocalDateTime startedAt;

    /**
     * When the task completed
     */
    private LocalDateTime completedAt;

    /**
     * Estimated duration in seconds
     */
    private Integer estimatedDurationSeconds;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
