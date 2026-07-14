package io.novel2video.agent.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色形象实体（子资产）
 */
@Data
public class GlobalCharacterAppearance {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * UUID
     */
    private String appearanceId;

    /**
     * 所属角色ID
     */
    private String characterId;

    // ========== 形象标识 ==========

    /**
     * 形象序号（0=默认）
     */
    private Integer appearanceIndex;

    /**
     * 形象说明（如"便装"、"战甲"）
     */
    private String changeReason;

    // ========== 风格与描述 ==========

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 当前使用的描述词
     */
    private String description;

    /**
     * 历史描述词列表（JSON数组）
     */
    private List<String> descriptions;

    /**
     * 描述来源: user | ai
     */
    private String descriptionSource;

    // ========== 当前图片 ==========

    /**
     * 当前显示的图片URL（单张，兼容旧数据）
     */
    private String imageUrl;

    /**
     * 当前图片媒体ID
     */
    private String imageMediaId;

    // ========== 候选图片 ==========

    /**
     * 候选图片列表（JSON数组）
     */
    private List<String> imageUrls;

    /**
     * 当前选中的图片索引
     */
    private Integer selectedIndex;

    // ========== 撤销支持 ==========

    /**
     * 上一次的图片URL
     */
    private String previousImageUrl;

    /**
     * 上一次的图片媒体ID
     */
    private String previousImageMediaId;

    /**
     * 上一次候选列表（JSON）
     */
    private List<String> previousImageUrls;

    /**
     * 上一次描述列表（JSON）
     */
    private List<String> previousDescriptions;

    // ========== 时间戳 ==========

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
