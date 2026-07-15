package io.novel2video.agent.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储服务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements StorageService {

    @Value("${storage.local.dir:./data/storage}")
    private String storageDir;

    @Value("${storage.local.url-prefix:/files/}")
    private String urlPrefix;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(storageDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created local storage directory: {}", path.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Failed to initialize local storage directory", e);
        }
    }

    @Override
    public String upload(String objectName, InputStream inputStream, String contentType, long size) {
        Path filePath = Paths.get(storageDir, objectName);
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            try (OutputStream os = Files.newOutputStream(filePath)) {
                FileCopyUtils.copy(inputStream, os);
            }
            log.info("Uploaded file locally: {}", filePath.toAbsolutePath());
            return getPublicUrl(objectName);
        } catch (IOException e) {
            log.error("Failed to upload file locally: {}", objectName, e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public String upload(String objectName, byte[] data, String contentType) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            return upload(objectName, inputStream, contentType, data.length);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public InputStream download(String objectName) {
        Path filePath = Paths.get(storageDir, objectName);
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("Failed to download file locally: {}", objectName, e);
            throw new RuntimeException("File download failed", e);
        }
    }

    @Override
    public String getPublicUrl(String objectName) {
        return urlPrefix + objectName;
    }

    @Override
    public String getPresignedUrl(String objectName) {
        return getPublicUrl(objectName);
    }

    @Override
    public void delete(String objectName) {
        Path filePath = Paths.get(storageDir, objectName);
        try {
            Files.deleteIfExists(filePath);
            log.info("Deleted local file: {}", objectName);
        } catch (IOException e) {
            log.error("Failed to delete local file: {}", objectName, e);
        }
    }

    @Override
    public boolean exists(String objectName) {
        return Files.exists(Paths.get(storageDir, objectName));
    }
}
