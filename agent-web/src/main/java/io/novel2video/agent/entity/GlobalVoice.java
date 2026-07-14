package io.novel2video.agent.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 全局音色实体
 */
@Data
public class GlobalVoice {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * UUID，业务主键
     */
    private String voiceId;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 可选：归属文件夹
     */
    private String folderId;

    // ========== 基本信息 ==========

    /**
     * 音色名称（必填）
     */
    private String name;

    /**
     * 音色描述
     */
    private String description;

    /**
     * 性别: male | female | neutral
     */
    private String gender;

    /**
     * 语言，默认中文
     */
    private String language;

    // ========== 音色类型 ==========

    /**
     * 音色类型: qwen-designed | custom
     * - qwen-designed: AI 设计的音色
     * - custom: 用户上传的音频
     */
    private String voiceType;

    // ========== qwen-tts 音色 ==========

    /**
     * qwen-tts 生成的音色 ID
     */
    private String qwenVoiceId;

    // ========== 自定义音频 ==========

    /**
     * 上传的音频 URL
     */
    private String customVoiceUrl;

    /**
     * 上传音频媒体ID
     */
    private String customVoiceMediaId;

    // ========== AI 设计参数 ==========

    /**
     * AI 设计时使用的提示词
     */
    private String voicePrompt;

    // ========== 时间戳 ==========

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
