package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传临时文件响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTempResponse {

    /**
     * 签名 URL（1小时有效）
     */
    private String url;

    /**
     * MinIO 存储路径
     */
    private String key;
}