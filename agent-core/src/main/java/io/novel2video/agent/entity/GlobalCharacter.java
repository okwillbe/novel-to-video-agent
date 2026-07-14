package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global Character Entity - 角色
 */
@Data
@Accessors(chain = true)
@TableName(value = "global_characters", autoResultMap = true)
public class GlobalCharacter {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String characterId;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 归属文件夹（可选）
     */
    private String folderId;

    /**
     * 角色名
     */
    private String name;

    /**
     * 别名（JSON数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> aliases;

    /**
     * 档案数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object profileData;

    /**
     * 档案是否已确认
     */
    private Integer profileConfirmed;

    /**
     * 音色ID (qwen-tts)
     */
    private String voiceId;

    /**
     * 音色类型: qwen-designed | custom
     */
    private String voiceType;

    /**
     * 上传的音频URL
     */
    private String customVoiceUrl;

    /**
     * 绑定的全局音色ID
     */
    private String globalVoiceId;

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