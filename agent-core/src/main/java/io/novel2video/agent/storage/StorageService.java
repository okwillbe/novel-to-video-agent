package io.novel2video.agent.storage;

import java.io.InputStream;

/**
 * 存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     *
     * @param objectName 对象名称（路径）
     * @param inputStream 文件流
     * @param contentType 内容类型
     * @param size 文件大小
     * @return 访问 URL
     */
    String upload(String objectName, InputStream inputStream, String contentType, long size);

    /**
     * 上传小文件（字节数组）
     *
     * @param objectName 对象名称（路径）
     * @param data 文件字节
     * @param contentType 内容类型
     * @return 访问 URL
     */
    String upload(String objectName, byte[] data, String contentType);

    /**
     * 获取文件流
     *
     * @param objectName 对象名称
     * @return 文件流
     */
    InputStream download(String objectName);

    /**
     * 获取公开访问 URL
     *
     * @param objectName 对象名称
     * @return URL
     */
    String getPublicUrl(String objectName);

    /**
     * 获取预签名 URL（有效期 7 天）
     *
     * @param objectName 对象名称
     * @return 预签名 URL
     */
    String getPresignedUrl(String objectName);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean exists(String objectName);

    /**
     * 生成对象名称
     *
     * @param prefix 前缀
     * @param extension 扩展名
     * @return 对象名称
     */
    default String generateObjectName(String prefix, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss")
        );
        String uuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s/%s_%s.%s", prefix, timestamp, uuid, extension);
    }
}
