package io.novel2video.agent.storage;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 存储服务
 *
 * 用于存储生成的图片、视频、音频等文件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;

    @Value("${storage.minio.bucket:novel2video}")
    private String bucket;

    /**
     * 初始化 bucket
     */
    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket", e);
        }
    }

    /**
     * 上传文件
     *
     * @param objectName 对象名称（路径）
     * @param inputStream 文件流
     * @param contentType 内容类型
     * @return 访问 URL
     */
    public String upload(String objectName, InputStream inputStream, String contentType, long size) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            log.info("Uploaded file: {} to bucket: {}", objectName, bucket);
            return getPublicUrl(objectName);
        } catch (Exception e) {
            log.error("Failed to upload file: {}", objectName, e);
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * 上传小文件（字节数组）
     */
    public String upload(String objectName, byte[] data, String contentType) {
        try (InputStream inputStream = new java.io.ByteArrayInputStream(data)) {
            return upload(objectName, inputStream, contentType, data.length);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    /**
     * 获取文件
     */
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to download file: {}", objectName, e);
            throw new RuntimeException("File download failed", e);
        }
    }

    /**
     * 获取预签名 URL（有效期 7 天）
     */
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get presigned URL for: {}", objectName, e);
            throw new RuntimeException("Failed to get presigned URL", e);
        }
    }

    /**
     * 获取公开访问 URL
     * 注意：需要 bucket 设置为公开或配置访问策略
     */
    public String getPublicUrl(String objectName) {
        // 简单实现：返回预签名 URL
        // 生产环境应配置 bucket 策略使其公开可读
        return getPresignedUrl(objectName);
    }

    /**
     * 删除文件
     */
    public void delete(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            log.info("Deleted file: {}", objectName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", objectName, e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成对象名称
     */
    public static String generateObjectName(String prefix, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss")
        );
        String uuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s/%s_%s.%s", prefix, timestamp, uuid, extension);
    }
}
