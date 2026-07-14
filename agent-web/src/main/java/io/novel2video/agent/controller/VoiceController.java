package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.GlobalVoice;
import io.novel2video.agent.service.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 音色控制器
 */
@Tag(name = "Voice API", description = "全局音色管理")
@RestController
@RequestMapping("/api/v1/assets/voices")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    // 单用户模式默认用户ID
    private static final String DEFAULT_USER_ID = "default-user";

    @Operation(summary = "获取音色列表")
    @GetMapping
    public ResponseEntity<ApiResponse<List<VoiceResponse>>> listVoices(
            @RequestParam(required = false) String folderId) {
        List<GlobalVoice> voices = voiceService.listVoices(DEFAULT_USER_ID, folderId);
        List<VoiceResponse> response = voices.stream()
                .map(VoiceResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取单个音色")
    @GetMapping("/{voiceId}")
    public ResponseEntity<ApiResponse<VoiceResponse>> getVoice(
            @PathVariable String voiceId) {
        GlobalVoice voice = voiceService.getVoice(voiceId);
        return ResponseEntity.ok(ApiResponse.success(VoiceResponse.from(voice)));
    }

    @Operation(summary = "创建音色")
    @PostMapping
    public ResponseEntity<ApiResponse<VoiceResponse>> createVoice(
            @RequestBody @Valid CreateVoiceRequest request) {
        GlobalVoice voice = voiceService.createVoice(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(VoiceResponse.from(voice)));
    }

    @Operation(summary = "更新音色")
    @PutMapping("/{voiceId}")
    public ResponseEntity<ApiResponse<VoiceResponse>> updateVoice(
            @PathVariable String voiceId,
            @RequestBody UpdateVoiceRequest request) {
        GlobalVoice voice = voiceService.updateVoice(voiceId, request);
        return ResponseEntity.ok(ApiResponse.success(VoiceResponse.from(voice)));
    }

    @Operation(summary = "删除音色")
    @DeleteMapping("/{voiceId}")
    public ResponseEntity<ApiResponse<Void>> deleteVoice(
            @PathVariable String voiceId) {
        voiceService.deleteVoice(voiceId, DEFAULT_USER_ID);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "AI 设计音色")
    @PostMapping("/design")
    public ResponseEntity<ApiResponse<VoiceDesignResponse>> designVoice(
            @RequestBody @Valid VoiceDesignRequest request) {
        VoiceDesignResponse response = voiceService.designVoice(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "音色选择器")
    @GetMapping("/picker")
    public ResponseEntity<ApiResponse<List<VoicePickerItem>>> getVoicePicker() {
        List<GlobalVoice> voices = voiceService.getVoicePicker(DEFAULT_USER_ID);
        List<VoicePickerItem> items = voices.stream()
                .map(VoicePickerItem::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(items));
    }
}