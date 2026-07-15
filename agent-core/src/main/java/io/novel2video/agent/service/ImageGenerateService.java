package io.novel2video.agent.service;

import io.novel2video.agent.dto.GenerateImageRequest;
import io.novel2video.agent.entity.GlobalCharacter;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.entity.GlobalLocation;
import io.novel2video.agent.enums.TargetType;
import io.novel2video.agent.enums.TaskType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 图片生成服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerateService {

    private final CharacterService characterService;
    private final LocationService locationService;

    // 系统提示词后缀
    private static final String CHARACTER_PROMPT_SUFFIX =
        "角色设定图，全身像，正面视角，清晰的面部特征，三视图，高质量，详细细节";

    private static final String LOCATION_PROMPT_SUFFIX =
        "场景设定图，全景视角，环境细节丰富，高质量，详细细节";

    /**
     * 提交图片生成任务
     */
    @Transactional
    public String generateImage(String userId, GenerateImageRequest request) {
        // 1. 读取资产信息
        AssetInfo asset = getAssetInfo(userId, request);

        // 2. 构建提示词
        String prompt = buildPrompt(asset, request.getArtStyle());

        // 3. 创建任务（简化版，实际需要任务队列）
        String taskId = UUID.randomUUID().toString();
        log.info("Created image generation task: {} for asset: {}", taskId, request.getId());

        // TODO: 实际提交到任务队列
        // Task task = taskService.createTask(...);
        // taskService.submitToQueue(task);

        return taskId;
    }

    /**
     * 获取资产信息
     */
    private AssetInfo getAssetInfo(String userId, GenerateImageRequest request) {
        if ("character".equals(request.getType())) {
            GlobalCharacter character = characterService.getCharacter(request.getId());
            List<GlobalCharacterAppearance> appearances =
                characterService.getAppearances(request.getId());
            GlobalCharacterAppearance appearance = appearances.get(
                request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0);

            return new AssetInfo(
                "character",
                character.getName(),
                appearance.getDescriptions() != null ? appearance.getDescriptions() :
                    (appearance.getDescription() != null ? List.of(appearance.getDescription()) : List.of()),
                appearance.getArtStyle()
            );
        } else {
            GlobalLocation location = locationService.getLocation(request.getId());
            return new AssetInfo(
                "location",
                location.getName(),
                location.getSummary() != null ? List.of(location.getSummary()) : List.of(),
                location.getArtStyle()
            );
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(AssetInfo asset, String artStyle) {
        StringBuilder prompt = new StringBuilder();

        // 用户描述词
        String description = String.join("，", asset.getDescriptions());
        prompt.append(description);

        // 系统后缀
        if ("character".equals(asset.getType())) {
            prompt.append("，").append(CHARACTER_PROMPT_SUFFIX);
        } else {
            prompt.append("，").append(LOCATION_PROMPT_SUFFIX);
        }

        // 艺术风格
        if (artStyle != null && !artStyle.isEmpty()) {
            prompt.append("，").append(artStyle);
        } else if (asset.getArtStyle() != null) {
            prompt.append("，").append(asset.getArtStyle());
        }

        return prompt.toString();
    }

    /**
     * 获取宽高比
     */
    public String getAspectRatio(String type) {
        return "character".equals(type) ? "3:2" : "1:1";
    }

    /**
     * 获取目标类型
     */
    public TargetType getTargetType(String type) {
        return "character".equals(type) ? TargetType.GLOBAL_CHARACTER : TargetType.GLOBAL_LOCATION;
    }

    /**
     * 资产信息内部类
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class AssetInfo {
        private String type;
        private String name;
        private List<String> descriptions;
        private String artStyle;
    }
}