package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新音色请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVoiceRequest {

    /**
     * 音色名称
     */
    private String name;

    /**
     * 音色描述
     */
    private String description;

    /**
     * 归属文件夹，null 移至未分类
     */
    private String folderId;

    /**
     * qwen-tts 音色ID
     */
    private String qwenVoiceId;

    /**
     * 音色类型
     */
    private String voiceType;

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
    private String language;
}
