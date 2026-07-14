package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 修改图片请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyImageRequest {

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
     * 要修改的图片索引
     */
    private Integer imageIndex;

    /**
     * 修改指令（如"把衣服改成蓝色"）
     */
    @NotBlank(message = "修改指令不能为空")
    private String modifyPrompt;

    /**
     * 参考图URL列表（可选）
     */
    private List<String> extraImageUrls;
}
