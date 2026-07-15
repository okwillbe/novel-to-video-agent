package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤销图片请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoImageRequest {

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
     * 形象序号（角色必填）
     */
    private Integer appearanceIndex;

    /**
     * 撤销单张图片（可选）
     */
    private Integer imageIndex;
}
