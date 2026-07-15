package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传临时文件请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTempRequest {

    /**
     * 图片模式：从 data URL 提取
     * 格式: data:image/png;base64,xxx
     */
    private String imageBase64;

    /**
     * 通用模式（音频等）
     */
    private String base64;

    /**
     * 文件扩展名（通用模式必需）
     */
    private String extension;
}