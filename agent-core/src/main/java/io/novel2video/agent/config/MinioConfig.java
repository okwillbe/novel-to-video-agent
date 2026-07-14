package io.novel2video.agent.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO Storage Configuration
 *
 * Used for storing generated images, videos, audio, and documents.
 * Mirrors waoowaoo's MinIO storage setup.
 */
@Configuration
public class MinioConfig {

    @Value("${storage.minio.endpoint:http://minio:9000}")
    private String endpoint;

    @Value("${storage.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${storage.minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${storage.minio.bucket:novel2video}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public String minioBucket() {
        return bucket;
    }
}
