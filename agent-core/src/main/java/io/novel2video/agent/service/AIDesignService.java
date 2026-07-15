package io.novel2video.agent.service;

import io.novel2video.agent.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 设计服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIDesignService {

    // 系统提示词后缀（用于移除）
    private static final String CHARACTER_PROMPT_SUFFIX =
        "角色设定图，全身像，正面视角，清晰的面部特征，三视图，高质量，详细细节";

    /**
     * AI 设计角色描述
     */
    public AIDesignCharacterResponse designCharacter(String userId, AIDesignCharacterRequest request) {
        // TODO: 调用 LLM API
        log.info("Designing character description for user: {}", userId);

        // 模拟返回（实际需要调用 AI Provider）
        String prompt = generateCharacterPrompt(request.getUserInstruction());

        return new AIDesignCharacterResponse(prompt);
    }

    /**
     * AI 设计场景描述
     */
    public AIDesignLocationResponse designLocation(String userId, AIDesignLocationRequest request) {
        log.info("Designing location description for user: {}", userId);

        // 模拟返回（实际需要调用 AI Provider）
        String prompt = generateLocationPrompt(request.getUserInstruction());

        return new AIDesignLocationResponse(prompt);
    }

    /**
     * AI 修改角色描述
     */
    public AIModifyCharacterResponse modifyCharacter(String userId, AIModifyCharacterRequest request) {
        log.info("Modifying character description for user: {}", userId);

        // 移除系统后缀
        String cleanDescription = removeCharacterPromptSuffix(request.getCurrentDescription());

        // TODO: 调用 LLM API 修改
        String modifiedDescription = modifyPrompt(cleanDescription, request.getModifyInstruction());

        return new AIModifyCharacterResponse(modifiedDescription);
    }

    /**
     * AI 修改场景描述
     */
    public AIModifyLocationResponse modifyLocation(String userId, AIModifyLocationRequest request) {
        log.info("Modifying location description for user: {}", userId);

        // TODO: 调用 LLM API 修改
        String modifiedDescription = modifyPrompt(request.getCurrentDescription(), request.getModifyInstruction());

        return new AIModifyLocationResponse(modifiedDescription);
    }

    /**
     * 生成角色提示词（模拟）
     */
    private String generateCharacterPrompt(String userInstruction) {
        // 实际应该调用 LLM
        return userInstruction + "，" + CHARACTER_PROMPT_SUFFIX;
    }

    /**
     * 生成场景提示词（模拟）
     */
    private String generateLocationPrompt(String userInstruction) {
        // 实际应该调用 LLM
        return userInstruction + "，场景设定图，全景视角，环境细节丰富，高质量，详细细节";
    }

    /**
     * 修改提示词（模拟）
     */
    private String modifyPrompt(String currentDescription, String modifyInstruction) {
        // 实际应该调用 LLM
        return currentDescription + "，" + modifyInstruction;
    }

    /**
     * 移除角色提示词后缀
     */
    private String removeCharacterPromptSuffix(String description) {
        return description.replace(CHARACTER_PROMPT_SUFFIX, "").trim();
    }
}