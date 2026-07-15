package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Global Voice Entity - 音色
 */
@Data
@Accessors(chain = true)
@TableName("global_voices")
public class GlobalVoice {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String voiceId;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 归属文件夹（可选）
     */
    private String folderId;

    /**
     * 音色名称
     */
    private String name;

    /**
     * 音色描述
     */
    private String description;

    /**
     * qwen-tts 生成的音色ID
     */
    private String qwenVoiceId;

    /**
     * 音色类型: qwen-designed | custom
     */
    private String voiceType;

    /**
     * 上传的音频URL（预览）
     */
    private String customVoiceUrl;

    /**
     * 上传的音频媒体ID
     */
    private String customVoiceMediaId;

    /**
     * AI设计时的提示词
     */
    private String voicePrompt;

    /**
     * 性别: male | female | neutral
     */
    private String gender;

    /**
     * 语言，默认 zh
     */
    private String language;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}