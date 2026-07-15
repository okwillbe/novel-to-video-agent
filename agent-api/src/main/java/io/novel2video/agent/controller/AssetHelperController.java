package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 辅助接口控制器
 */
@Tag(name = "Asset Helper API", description = "资产辅助接口")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetHelperController {

    private final ImageUploadService imageUploadService;
    private final AssetPickerService assetPickerService;
    private final ReferenceToCharacterService referenceToCharacterService;
    private final AssetLabelService assetLabelService;
    private final AppearanceService appearanceService;

    private static final String DEFAULT_USER_ID = "default-user";

    // ==================== 上传接口 ====================

    @Operation(summary = "上传图片")
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadImageResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("id") String id,
            @RequestParam(value = "appearanceIndex", required = false) Integer appearanceIndex,
            @RequestParam(value = "imageIndex", required = false) Integer imageIndex,
            @RequestParam("labelText") String labelText) {

        UploadImageRequest request = new UploadImageRequest(type, id, appearanceIndex, imageIndex, labelText);
        UploadImageResponse response = imageUploadService.uploadImage(DEFAULT_USER_ID, file, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "上传临时文件")
    @PostMapping("/upload-temp")
    public ResponseEntity<ApiResponse<UploadTempResponse>> uploadTemp(
            @RequestBody @Valid UploadTempRequest request) {
        UploadTempResponse response = imageUploadService.uploadTemp(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 选择/撤销接口 ====================

    @Operation(summary = "选择候选图片")
    @PostMapping("/select-image")
    public ResponseEntity<ApiResponse<Void>> selectImage(
            @RequestBody @Valid SelectImageRequest request) {
        if ("character".equals(request.getType())) {
            appearanceService.selectImage(request.getId(), request.getAppearanceIndex(), request.getImageIndex());
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "撤销图片修改")
    @PostMapping("/undo-image")
    public ResponseEntity<ApiResponse<Void>> undoImage(
            @RequestBody @Valid UndoImageRequest request) {
        if ("character".equals(request.getType())) {
            appearanceService.undoImage(request.getId(), request.getAppearanceIndex());
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== AI 生成接口 ====================

    @Operation(summary = "参考图生成角色")
    @PostMapping("/reference-to-character")
    public ResponseEntity<ApiResponse<ReferenceToCharacterResponse>> referenceToCharacter(
            @RequestBody @Valid ReferenceToCharacterRequest request) {
        ReferenceToCharacterResponse response = referenceToCharacterService.referenceToCharacter(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 其他接口 ====================

    @Operation(summary = "更新资产标签")
    @PostMapping("/update-asset-label")
    public ResponseEntity<ApiResponse<String>> updateAssetLabel(
            @RequestBody @Valid UpdateAssetLabelRequest request) {
        String url = assetLabelService.updateAssetLabel(DEFAULT_USER_ID, request);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    // ==================== 选择器 ====================

    @Operation(summary = "资产选择器")
    @GetMapping("/picker")
    public ResponseEntity<ApiResponse<AssetPickerResponse>> getPicker(
            @RequestParam(required = false) String type) {
        AssetPickerResponse response = assetPickerService.getPicker(DEFAULT_USER_ID, type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}