package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参考图生成角色请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceToCharacterRequest {

    /**
     * 参考图URL列表（最多5张）
     */
    private java.util.List<String> referenceImageUrls;

    /**
     * 角色ID
     */
    @NotBlank(message = "角色ID不能为空")
    private String characterId;

    /**
     * 形象ID
     */
    @NotBlank(message = "形像ID不能为空")
    private String appearanceId;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String characterName;

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 生成数量，默认4
     */
    private Integer count = 4;

    /**
     * 自定义描述词（覆盖提取）
     */
    private String customDescription;

    /**
     * 仅提取描述词，不生成图片
     */
    private Boolean extractOnly;
}