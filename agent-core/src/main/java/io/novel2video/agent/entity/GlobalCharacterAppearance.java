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
     * 历史描述词列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> descriptions;

    /**
     * 描述来源
     */
    private String descriptionSource;

    /**
     * 单张图URL
     */
    private String imageUrl;

    /**
     * 当前图片媒体ID
     */
    private String imageMediaId;

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
     * 上一次的图片URL
     */
    private String previousImageUrl;

    /**
     * 上一次的图片媒体ID
     */
    private String previousImageMediaId;

    /**
     * 上一次候选列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> previousImageUrls;

    /**
     * 上一次描述列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> previousDescriptions;

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