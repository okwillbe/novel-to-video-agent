package io.novel2video.agent.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 全局资产文件夹实体
 */
@Data
public class GlobalAssetFolder {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
