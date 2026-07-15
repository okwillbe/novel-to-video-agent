package io.novel2video.agent.service;

import io.novel2video.agent.dto.ModifyImageRequest;
import io.novel2video.agent.entity.GlobalCharacter;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.enums.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 图片修改服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageModifyService {

    private final CharacterService characterService;
    private final LocationService locationService;
    private final AppearanceService appearanceService;

    /**
     * 提交图片修改任务
     */
    @Transactional
    public String modifyImage(String userId, ModifyImageRequest request) {
        // 1. 获取目标图片
        String currentImageUrl = getCurrentImageUrl(userId, request);

        // 2. 准备参考图
        List<String> referenceImages = prepareReferenceImages(currentImageUrl, request.getExtraImageUrls());

        // 3. 构建修改提示词
        String prompt = buildModifyPrompt(request.getType(), request.getModifyPrompt());

        // 4. 创建任务（简化版）
        String taskId = UUID.randomUUID().toString();
        log.info("Created image modification task: {} for asset: {}", taskId, request.getId());

        // TODO: 实际提交到任务队列
        // 保存撤销状态
        if ("character".equals(request.getType())) {
            GlobalCharacterAppearance appearance = characterService.getAppearance(
                request.getId(),
                request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0
            );
            appearanceService.savePreviousState(appearance.getAppearanceId());
        }

        return taskId;
    }

    /**
     * 获取当前图片 URL
     */
    private String getCurrentImageUrl(String userId, ModifyImageRequest request) {
        if ("character".equals(request.getType())) {
            GlobalCharacter character = characterService.getCharacter(request.getId());
            List<GlobalCharacterAppearance> appearances = characterService.getAppearances(request.getId());
            GlobalCharacterAppearance appearance = appearances.get(
                request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0);

            List<String> imageUrls = appearance.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                int index = request.getImageIndex() != null ? request.getImageIndex() : 0;
                return imageUrls.get(Math.min(index, imageUrls.size() - 1));
            }
            return appearance.getImageUrl();
        } else {
            // TODO: 场景图片处理
            return null;
        }
    }

    /**
     * 准备参考图
     */
    private List<String> prepareReferenceImages(String currentUrl, List<String> extraUrls) {
        List<String> references = new ArrayList<>();

        if (currentUrl != null) {
            references.add(currentUrl);
        }

        if (extraUrls != null && !extraUrls.isEmpty()) {
            references.addAll(extraUrls);
        }

        return references;
    }

    /**
     * 构建修改提示词
     */
    private String buildModifyPrompt(String type, String modifyPrompt) {
        if ("character".equals(type)) {
            return "请根据以下指令修改图片，保持人物核心特征一致：\n" + modifyPrompt;
        } else {
            return "请根据以下指令修改场景图片，保持整体风格一致：\n" + modifyPrompt;
        }
    }

    /**
     * 构建目标ID
     */
    public String buildTargetId(ModifyImageRequest request) {
        return String.format("%s:%d:%d",
            request.getId(),
            request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0,
            request.getImageIndex() != null ? request.getImageIndex() : 0);
    }

    /**
     * 获取修改目标类型
     */
    public TargetType getModifyTargetType(String type) {
        return "character".equals(type) ? TargetType.GLOBAL_CHARACTER_APPEARANCE : TargetType.GLOBAL_LOCATION_IMAGE;
    }
}