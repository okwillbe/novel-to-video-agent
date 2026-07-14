package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Global Location Image Entity - 场景图片（子资产）
 */
@Data
@Accessors(chain = true)
@TableName("global_location_images")
public class GlobalLocationImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String imageId;

    /**
     * 所属场景
     */
    private String locationId;

    /**
     * 图片序号
     */
    private Integer imageIndex;

    /**
     * 图片描述
     */
    private String description;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 是否选中
     */
    private Integer isSelected;

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