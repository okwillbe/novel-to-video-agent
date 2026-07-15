package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.service.AppearanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 形象控制器
 */
@Tag(name = "Appearance API", description = "角色形象管理")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AppearanceController {

    private final AppearanceService appearanceService;

    @Operation(summary = "创建形象")
    @PostMapping("/appearances")
    public ResponseEntity<ApiResponse<AppearanceResponse>> createAppearance(
            @RequestBody @Valid CreateAppearanceRequest request) {
        GlobalCharacterAppearance appearance = appearanceService.createAppearance(request);
        return ResponseEntity.ok(ApiResponse.success(AppearanceResponse.from(appearance)));
    }

    @Operation(summary = "获取角色的所有形象")
    @GetMapping("/characters/{characterId}/appearances")
    public ResponseEntity<ApiResponse<List<AppearanceResponse>>> getAppearances(
            @PathVariable String characterId) {
        List<GlobalCharacterAppearance> appearances = appearanceService.getAppearancesByCharacterId(characterId);
        List<AppearanceResponse> response = appearances.stream()
                .map(AppearanceResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取单个形象")
    @GetMapping("/characters/{characterId}/appearances/{appearanceIndex}")
    public ResponseEntity<ApiResponse<AppearanceResponse>> getAppearance(
            @PathVariable String characterId,
            @PathVariable Integer appearanceIndex) {
        GlobalCharacterAppearance appearance = appearanceService.getAppearance(characterId, appearanceIndex);
        return ResponseEntity.ok(ApiResponse.success(AppearanceResponse.from(appearance)));
    }

    @Operation(summary = "更新形象")
    @PatchMapping("/characters/{characterId}/appearances/{appearanceIndex}")
    public ResponseEntity<ApiResponse<AppearanceResponse>> updateAppearance(
            @PathVariable String characterId,
            @PathVariable Integer appearanceIndex,
            @RequestBody UpdateAppearanceRequest request) {
        GlobalCharacterAppearance appearance = appearanceService.updateAppearance(characterId, appearanceIndex, request);
        return ResponseEntity.ok(ApiResponse.success(AppearanceResponse.from(appearance)));
    }

    @Operation(summary = "删除形象")
    @DeleteMapping("/appearances/{appearanceId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppearance(
            @PathVariable String appearanceId) {
        appearanceService.deleteAppearance(appearanceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}