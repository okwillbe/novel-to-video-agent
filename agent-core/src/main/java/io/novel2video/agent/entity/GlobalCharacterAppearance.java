package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global Character Appearance Entity - 角色形象（子资产）
 */
@Data
@Accessors(chain = true)
@TableName(value = "global_character_appearances", autoResultMap = true)
public class GlobalCharacterAppearance {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String appearanceId;

    /**
     * 所属角色
     */
    private String characterId;

    /**
     * 形象序号
     */
    private Integer appearanceIndex;

    /**
     * 形象说明（如"便装"、"战甲"）
     */
    private String changeReason;

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 形象描述
     */
    private String description;

    /**
     * 单张图URL
     */
    private String imageUrl;

    /**
     * 多图URL（JSON数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> imageUrls;

    /**
     * 当前选中的图片索引
     */
    private Integer selectedIndex;

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