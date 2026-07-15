package io.novel2video.agent.service;

import io.novel2video.agent.dto.ReferenceToCharacterRequest;
import io.novel2video.agent.dto.ReferenceToCharacterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 参考图生成角色服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceToCharacterService {

    private final CharacterService characterService;
    private final AppearanceService appearanceService;

    /**
     * 参考图生成角色
     */
    @Transactional
    public ReferenceToCharacterResponse referenceToCharacter(String userId, ReferenceToCharacterRequest request) {
        // 创建任务（简化版）
        String taskId = UUID.randomUUID().toString();
        log.info("Created reference-to-character task: {} for character: {}", taskId, request.getCharacterId());

        // 提取模式
        if (Boolean.TRUE.equals(request.getExtractOnly())) {
            String description = extractDescription(request.getReferenceImageUrls(), request.getCharacterName());
            return new ReferenceToCharacterResponse(taskId, "completed", null, description);
        }

        // 生成模式（需要异步任务）
        return new ReferenceToCharacterResponse(taskId, "queued", null, null);
    }

    /**
     * 从参考图提取描述词（模拟）
     */
    private String extractDescription(List<String> referenceUrls, String characterName) {
        // TODO: 调用 AI Vision API
        log.info("Extracting description from {} reference images", referenceUrls.size());
        return characterName + "的外观描述（从参考图提取）";
    }

    /**
     * 处理参考图生成任务（Worker 调用）
     */
    @Transactional
    public void handleReferenceToCharacterTask(String taskId, ReferenceToCharacterRequest request) {
        // 构建提示词
        String prompt = buildPrompt(request);

        // 生成图片
        List<String> generatedUrls = new ArrayList<>();
        for (int i = 0; i < request.getCount(); i++) {
            // TODO: 调用图片生成 API
            String url = "https://mock-minio.example.com/characters/" + request.getCharacterId() + "/" + i + ".jpg";
            generatedUrls.add(url);
        }

        // 更新形象
        appearanceService.saveGeneratedImages(request.getAppearanceId(), generatedUrls);

        log.info("Generated {} images for character {}", generatedUrls.size(), request.getCharacterId());
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(ReferenceToCharacterRequest request) {
        StringBuilder prompt = new StringBuilder();

        if (request.getCustomDescription() != null) {
            prompt.append(request.getCustomDescription());
        } else {
            prompt.append("根据参考图生成角色三视图，正面、侧面、背面");
        }

        prompt.append("，角色设定图，全身像，正面视角，清晰的面部特征，三视图，高质量，详细细节");

        if (request.getArtStyle() != null) {
            prompt.append("，").append(request.getArtStyle());
        }

        return prompt.toString();
    }
}