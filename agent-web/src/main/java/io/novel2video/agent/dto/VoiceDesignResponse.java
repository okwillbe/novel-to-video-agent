package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 设计音色响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceDesignResponse {

    /**
     * qwen-tts 返回的音色ID
     */
    private String voiceId;

    /**
     * 预览音频（base64）
     */
    private String audioBase64;
}
