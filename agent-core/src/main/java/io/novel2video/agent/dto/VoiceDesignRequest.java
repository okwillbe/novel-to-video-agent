package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 设计音色请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceDesignRequest {

    /**
     * 说话人名称
     */
    @NotBlank(message = "说话人名称不能为空")
    private String speaker;

    /**
     * 设计提示词
     */
    private String prompt;

    /**
     * 是否已有音色（用于更新）
     */
    private Boolean hasExistingVoice;
}
