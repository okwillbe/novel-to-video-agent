package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Global Location Entity - 场景
 */
@Data
@Accessors(chain = true)
@TableName("global_locations")
public class GlobalLocation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String locationId;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 归属文件夹（可选）
     */
    private String folderId;

    /**
     * 场景名
     */
    private String name;

    /**
     * 场景简介
     */
    private String summary;

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}