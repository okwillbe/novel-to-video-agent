package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User Entity - User account information
 *
 * Initial version: single user (admin)
 * Future: multi-tenant support
 */
@Data
@Accessors(chain = true)
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String email;

    private String phone;

    private String passwordHash;

    private String nickname;

    private String avatarUrl;

    private String role;

    private Integer status;

    private BigDecimal balance;

    private Integer quotaVideoSeconds;

    private Integer quotaImageCount;

    private Integer quotaVoiceSeconds;

    private Long quotaTextTokens;

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object settings;

    private LocalDateTime lastLoginAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ===== 便捷方法 =====

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isActive() {
        return status != null && status == 1;
    }
}
