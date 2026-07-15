package io.novel2video.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.exception.NotFoundException;
import io.novel2video.agent.mapper.GlobalCharacterAppearanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 形象服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppearanceService {

    private final GlobalCharacterAppearanceMapper appearanceMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取角色的所有形象
     */
    public List<GlobalCharacterAppearance> getAppearancesByCharacterId(String characterId) {
        return appearanceMapper.findByCharacterId(characterId);
    }

    /**
     * 获取单个形象
     */
    public GlobalCharacterAppearance getAppearance(String characterId, Integer appearanceIndex) {
        GlobalCharacterAppearance appearance = appearanceMapper.findByCharacterIdAndIndex(characterId, appearanceIndex);
        if (appearance == null) {
            throw new NotFoundException("形象不存在");
        }
        return appearance;
    }

    /**
     * 创建形象
     */
    @Transactional
    public GlobalCharacterAppearance createAppearance(CreateAppearanceRequest request) {
        // 获取最大序号
        Integer maxIndex = appearanceMapper.getMaxAppearanceIndex(request.getCharacterId());
        int nextIndex = (maxIndex == null) ? 0 : maxIndex + 1;

        GlobalCharacterAppearance appearance = new GlobalCharacterAppearance();
        appearance.setAppearanceId(UUID.randomUUID().toString());
        appearance.setCharacterId(request.getCharacterId());
        appearance.setAppearanceIndex(request.getAppearanceIndex() != null ? request.getAppearanceIndex() : nextIndex);
        appearance.setChangeReason(request.getChangeReason());
        appearance.setArtStyle(request.getArtStyle());
        appearance.setDescription(request.getDescription());
        appearance.setImageUrl(request.getImageUrl());
        appearance.setSelectedIndex(0);

        appearanceMapper.insert(appearance);
        log.info("Created appearance: {} for character: {}", appearance.getAppearanceId(), request.getCharacterId());
        return appearance;
    }

    /**
     * 更新形象
     */
    public GlobalCharacterAppearance updateAppearance(String characterId, Integer appearanceIndex, UpdateAppearanceRequest request) {
        GlobalCharacterAppearance appearance = appearanceMapper.findByCharacterIdAndIndex(characterId, appearanceIndex);
        if (appearance == null) {
            throw new NotFoundException("形象不存在");
        }

        if (request.getChangeReason() != null) {
            appearance.setChangeReason(request.getChangeReason());
        }
        if (request.getDescription() != null) {
            appearance.setDescription(request.getDescription());
        }
        if (request.getArtStyle() != null) {
            appearance.setArtStyle(request.getArtStyle());
        }
        if (request.getSelectedIndex() != null) {
            appearance.setSelectedIndex(request.getSelectedIndex());
        }

        appearanceMapper.update(appearance);
        log.info("Updated appearance: {}", appearance.getAppearanceId());
        return appearance;
    }

    /**
     * 删除形象
     */
    @Transactional
    public void deleteAppearance(String appearanceId) {
        appearanceMapper.deleteByAppearanceId(appearanceId);
        log.info("Deleted appearance: {}", appearanceId);
    }

    /**
     * 选择图片
     */
    public void selectImage(String characterId, Integer appearanceIndex, Integer imageIndex) {
        GlobalCharacterAppearance appearance = appearanceMapper.findByCharacterIdAndIndex(characterId, appearanceIndex);
        if (appearance == null) {
            throw new NotFoundException("形象不存在");
        }

        appearanceMapper.updateSelectedIndex(appearance.getAppearanceId(), imageIndex);
        log.info("Selected image {} for appearance {}", imageIndex, appearance.getAppearanceId());
    }

    /**
     * 保存生成结果
     */
    @Transactional
    public void saveGeneratedImages(String appearanceId, List<String> imageUrls) {
        try {
            String imageUrlsJson = objectMapper.writeValueAsString(imageUrls);
            appearanceMapper.updateImageUrls(appearanceId, imageUrlsJson);
            log.info("Saved {} generated images for appearance {}", imageUrls.size(), appearanceId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize image URLs", e);
            throw new RuntimeException("保存图片失败");
        }
    }

    /**
     * 保存修改前的状态（用于撤销）
     */
    @Transactional
    public void savePreviousState(String appearanceId) {
        GlobalCharacterAppearance appearance = appearanceMapper.selectByAppearanceId(appearanceId);
        if (appearance == null) {
            return;
        }

        try {
            String previousImageUrls = appearance.getImageUrls() != null
                    ? objectMapper.writeValueAsString(appearance.getImageUrls()) : null;
            String previousDescriptions = appearance.getDescriptions() != null
                    ? objectMapper.writeValueAsString(appearance.getDescriptions()) : null;

            appearanceMapper.savePreviousState(
                    appearanceId,
                    appearance.getImageUrl(),
                    appearance.getImageMediaId(),
                    previousImageUrls,
                    previousDescriptions
            );
            log.info("Saved previous state for appearance: {}", appearanceId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize previous state", e);
        }
    }

    /**
     * 撤销图片修改
     */
    @Transactional
    public void undoImage(String characterId, Integer appearanceIndex) {
        GlobalCharacterAppearance appearance = appearanceMapper.findByCharacterIdAndIndex(characterId, appearanceIndex);
        if (appearance == null) {
            throw new NotFoundException("形象不存在");
        }

        appearanceMapper.restoreFromPreviousState(appearance.getAppearanceId());
        log.info("Undid image modification for appearance: {}", appearance.getAppearanceId());
    }

    /**
     * 处理图片生成任务完成
     */
    @Transactional
    public void handleImageGenerated(String appearanceId, List<String> imageUrls) {
        saveGeneratedImages(appearanceId, imageUrls);
    }

    /**
     * 处理图片修改任务完成
     */
    @Transactional
    public void handleImageModified(String appearanceId, Integer imageIndex, String newImageUrl) {
        GlobalCharacterAppearance appearance = appearanceMapper.selectByAppearanceId(appearanceId);
        if (appearance == null) {
            return;
        }

        // 更新指定索引的图片
        List<String> urls = appearance.getImageUrls();
        if (urls != null && imageIndex < urls.size()) {
            urls.set(imageIndex, newImageUrl);
            try {
                String imageUrlsJson = objectMapper.writeValueAsString(urls);
                appearanceMapper.updateImageUrls(appearanceId, imageUrlsJson);
                log.info("Modified image {} for appearance {}", imageIndex, appearanceId);
            } catch (JsonProcessingException e) {
                log.error("Failed to update image", e);
            }
        }
    }
}