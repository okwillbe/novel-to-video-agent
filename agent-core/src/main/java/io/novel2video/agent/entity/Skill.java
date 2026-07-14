package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Skill Entity - Corresponds to skills table
 *
 * Skills are reusable prompts/workflows stored in MySQL,
 * following agentscope-java's Markdown + YAML frontmatter format.
 */
@Data
@Accessors(chain = true)
@TableName("skills")
public class Skill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Unique skill identifier, e.g. "novel-analysis_v1"
     */
    private String skillId;

    /**
     * Skill display name
     */
    private String name;

    /**
     * Skill description
     */
    private String description;

    /**
     * Category: analysis/generation/synthesis/postprocess
     */
    private String category;

    /**
     * Skill content in Markdown format with YAML frontmatter
     */
    private String skillContent;

    /**
     * Version number
     */
    private String version;

    /**
     * Author
     */
    private String author;

    /**
     * Tags list (JSON)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * Extended metadata (JSON)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object metadata;

    /**
     * Status: 0-disabled, 1-enabled, 2-draft
     */
    private Integer status;

    /**
     * Public: 0-private, 1-public
     */
    private Integer isPublic;

    /**
     * Owner ID for private skills
     */
    private String ownerId;

    /**
     * Tenant ID for multi-tenant isolation
     */
    private String tenantId;

    /**
     * Usage count
     */
    private Long useCount;

    /**
     * Success count
     */
    private Long successCount;

    /**
     * Average rating (1.00 - 5.00)
     */
    private Double avgRating;

    /**
     * Created timestamp
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * Updated timestamp
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
