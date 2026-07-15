package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传图片请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageRequest {

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
     * 形象序号（仅角色）
     */
    private Integer appearanceIndex;

    /**
     * 图片索引
     */
    private Integer imageIndex;

    /**
     * 黑边标签文字
     */
    @NotBlank(message = "标签文字不能为空")
    private String labelText;
}