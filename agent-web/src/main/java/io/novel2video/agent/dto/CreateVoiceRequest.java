package io.novel2video.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建音色请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoiceRequest {

    /**
     * 音色名称
     */
    @NotBlank(message = "音色名称不能为空")
    private String name;

    /**
     * 音色描述
     */
    private String description;

    /**
     * 归属文件夹
     */
    private String folderId;

    /**
     * qwen-tts 音色ID
     */
    private String qwenVoiceId;

    /**
     * 音色类型，默认 qwen-designed
     */
    private String voiceType = "qwen-designed";

    /**
     * AI 设计时使用的提示词
     */
    private String voicePrompt;

    /**
     * 自定义音频 URL
     */
    private String customVoiceUrl;

    /**
     * 性别
     */
    private String gender;

    /**
     * 语言
     */
    private String language = "zh";
}
