package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 设计场景描述请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIDesignLocationRequest {

    /**
     * 用户设计指令
     */
    @NotBlank(message = "用户指令不能为空")
    private String userInstruction;
}
