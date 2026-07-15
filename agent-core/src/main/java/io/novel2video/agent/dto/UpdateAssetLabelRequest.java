package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新资产标签请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetLabelRequest {

    /**
     * 类型: character | location
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 资产ID
     */
    @NotBlank(message = "ID不能为空")
    private String id;

    /**
     * 新名称
     */
    @NotBlank(message = "名称不能为空")
    private String newName;

    /**
     * 形象序号（仅更新指定形象）
     */
    private Integer appearanceIndex;
}