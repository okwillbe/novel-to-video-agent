package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * TaskStep Entity - Individual step execution tracking
 */
@Data
@Accessors(chain = true)
@TableName("task_steps")
public class TaskStep {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskId;

    private Integer stepIndex;

    private String stepName;

    private String skillId;

    private String status;

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object inputData;

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object outputData;

    private String errorMessage;

    private Integer progress;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer executionTimeMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
