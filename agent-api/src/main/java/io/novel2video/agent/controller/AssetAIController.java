package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 资产 AI 生成和修改控制器
 */
@Tag(name = "Asset AI API", description = "资产 AI 生成和修改")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetAIController {

    private final ImageGenerateService imageGenerateService;
    private final ImageModifyService imageModifyService;
    private final AIDesignService aiDesignService;

    private static final String DEFAULT_USER_ID = "default-user";

    // ==================== 图片生成 ====================

    @Operation(summary = "生成图片")
    @PostMapping("/generate-image")
    public ResponseEntity<ApiResponse<GenerateImageResponse>> generateImage(
            @RequestBody @Valid GenerateImageRequest request) {
        String taskId = imageGenerateService.generateImage(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(
            new GenerateImageResponse(taskId, "queued")));
    }

    // ==================== 图片修改 ====================

    @Operation(summary = "修改图片")
    @PostMapping("/modify-image")
    public ResponseEntity<ApiResponse<ModifyImageResponse>> modifyImage(
            @RequestBody @Valid ModifyImageRequest request) {
        String taskId = imageModifyService.modifyImage(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(
            new ModifyImageResponse(taskId, "queued")));
    }

    // ==================== AI 设计 ====================

    @Operation(summary = "AI 设计角色描述")
    @PostMapping("/ai-design-character")
    public ResponseEntity<ApiResponse<AIDesignCharacterResponse>> designCharacter(
            @RequestBody @Valid AIDesignCharacterRequest request) {
        AIDesignCharacterResponse response = aiDesignService.designCharacter(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 设计场景描述")
    @PostMapping("/ai-design-location")
    public ResponseEntity<ApiResponse<AIDesignLocationResponse>> designLocation(
            @RequestBody @Valid AIDesignLocationRequest request) {
        AIDesignLocationResponse response = aiDesignService.designLocation(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== AI 修改 ====================

    @Operation(summary = "AI 修改角色描述")
    @PostMapping("/ai-modify-character")
    public ResponseEntity<ApiResponse<AIModifyCharacterResponse>> modifyCharacter(
            @RequestBody @Valid AIModifyCharacterRequest request) {
        AIModifyCharacterResponse response = aiDesignService.modifyCharacter(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 修改场景描述")
    @PostMapping("/ai-modify-location")
    public ResponseEntity<ApiResponse<AIModifyLocationResponse>> modifyLocation(
            @RequestBody @Valid AIModifyLocationRequest request) {
        AIModifyLocationResponse response = aiDesignService.modifyLocation(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}