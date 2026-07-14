package io.novel2video.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Global Asset Folder Entity - 文件夹（组织单元）
 */
@Data
@Accessors(chain = true)
@TableName("global_asset_folders")
public class GlobalAssetFolder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * UUID
     */
    private String folderId;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sortOrder;

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