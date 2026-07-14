package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建形象请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppearanceRequest {

    /**
     * 所属角色ID（必填）
     */
    @NotBlank(message = "角色ID不能为空")
    private String characterId;

    /**
     * 形象序号（自动递增）
     */
    private Integer appearanceIndex;

    /**
     * 形象说明（必填）
     */
    @NotBlank(message = "形象说明不能为空")
    private String changeReason;

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 描述词
     */
    private String description;

    /**
     * 初始图片URL
     */
    private String initialImageUrl;
}
