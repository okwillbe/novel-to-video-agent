package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 修改角色描述请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIModifyCharacterRequest {

    /**
     * 当前描述词
     */
    @NotBlank(message = "当前描述不能为空")
    private String currentDescription;

    /**
     * 修改指令
     */
    @NotBlank(message = "修改指令不能为空")
    private String modifyInstruction;
}
