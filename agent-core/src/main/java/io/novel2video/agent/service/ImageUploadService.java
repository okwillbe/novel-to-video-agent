package io.novel2video.agent.service;

import io.novel2video.agent.dto.UploadImageRequest;
import io.novel2video.agent.dto.UploadImageResponse;
import io.novel2video.agent.dto.UploadTempRequest;
import io.novel2video.agent.dto.UploadTempResponse;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.entity.GlobalLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图片上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final CharacterService characterService;
    private final LocationService locationService;
    private final AppearanceService appearanceService;

    // TODO: 注入 MinioService
    // private final MinioService minioService;

    /**
     * 上传图片
     */
    @Transactional
    public UploadImageResponse uploadImage(String userId, MultipartFile file, UploadImageRequest request) {
        try {
            // 1. 模拟添加黑边标签（实际需要 MinioService）
            String imageUrl = "https://mock-minio.example.com/assets/" + request.getType() + "/" +
                request.getId() + "/" + System.currentTimeMillis() + ".jpg";
            String imageKey = "assets/" + request.getType() + "/" + request.getId() + "/" +
                System.currentTimeMillis() + ".jpg";

            // 2. 更新数据库
            updateAssetImageUrls(request, imageUrl);

            log.info("Uploaded image for {} {}: {}", request.getType(), request.getId(), imageUrl);

            return new UploadImageResponse(imageKey, request.getImageIndex(), imageUrl);

        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new RuntimeException("上传图片失败", e);
        }
    }

    /**
     * 更新资产图片 URL
     */
    private void updateAssetImageUrls(UploadImageRequest request, String imageUrl) {
        if ("character".equals(request.getType())) {
            GlobalCharacterAppearance appearance = characterService.getAppearance(
                request.getId(),
                request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0
            );

            // 更新图片列表
            List<String> urls = new ArrayList<>(appearance.getImageUrls() != null ? appearance.getImageUrls() : List.of());
            int index = request.getImageIndex() != null ? request.getImageIndex() : urls.size();
            if (index < urls.size()) {
                urls.set(index, imageUrl);
            } else {
                urls.add(imageUrl);
            }

            appearance.setImageUrls(urls);
            appearance.setSelectedIndex(index);
            characterService.updateAppearance(appearance);

        } else {
            GlobalLocation location = locationService.getLocation(request.getId());
            location.setImageUrl(imageUrl);
            locationService.updateLocation(location);
        }
    }

    /**
     * 上传临时文件
     */
    public UploadTempResponse uploadTemp(String userId, UploadTempRequest request) {
        byte[] data;
        String extension;

        if (request.getImageBase64() != null) {
            // 图片模式：从 data URL 解析
            Pattern pattern = Pattern.compile("^data:image/(\\w+);base64,(.+)$");
            Matcher matcher = pattern.matcher(request.getImageBase64());

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid image base64 format");
            }

            extension = matcher.group(1);
            String base64Data = matcher.group(2);
            data = Base64.getDecoder().decode(base64Data);

        } else if (request.getBase64() != null) {
            if (request.getExtension() == null) {
                throw new IllegalArgumentException("Extension is required for non-image files");
            }
            extension = request.getExtension();
            data = Base64.getDecoder().decode(request.getBase64());

        } else {
            throw new IllegalArgumentException("No data provided");
        }

        // 生成唯一路径
        String key = String.format("temp/%s/%d_%s.%s",
            userId, System.currentTimeMillis(), UUID.randomUUID().toString().substring(0, 8), extension);

        // TODO: 实际上传到 MinIO
        // minioService.upload(data, key);

        // 模拟签名 URL
        String signedUrl = "https://mock-minio.example.com/" + key + "?expires=3600";

        log.info("Uploaded temp file: {}", key);

        return new UploadTempResponse(signedUrl, key);
    }
}