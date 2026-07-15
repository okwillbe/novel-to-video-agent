package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传图片响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponse {

    /**
     * MinIO 存储路径
     */
    private String imageKey;

    /**
     * 图片索引
     */
    private Integer imageIndex;

    /**
     * 访问 URL
     */
    private String imageUrl;
}