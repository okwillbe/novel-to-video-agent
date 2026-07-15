package io.novel2video.agent.controller;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.*;
import io.novel2video.agent.service.AssetService;
import io.novel2video.agent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Asset API Controller - 资产管理 REST API
 */
@Tag(name = "Asset API", description = "全局资产管理（文件夹、角色、场景、音色）")
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final UserService userService;

    // 单用户模式：获取默认用户ID
    private String getDefaultUserId() {
        return userService.getOrCreateDefaultUser().getUserId();
    }

    // ==================== 文件夹操作 ====================

    @Operation(summary = "列出文件夹")
    @GetMapping("/folders")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> listFolders() {
        String userId = getDefaultUserId();
        List<GlobalAssetFolder> folders = assetService.listFolders(userId);
        List<FolderResponse> response = folders.stream()
                .map(this::toFolderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "创建文件夹")
    @PostMapping("/folders")
    public ResponseEntity<ApiResponse<FolderResponse>> createFolder(@RequestBody CreateFolderRequest request) {
        String userId = getDefaultUserId();
        GlobalAssetFolder folder = new GlobalAssetFolder()
                .setUserId(userId)
                .setName(request.getName())
                .setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        folder = assetService.createFolder(folder);
        return ResponseEntity.ok(ApiResponse.success(toFolderResponse(folder)));
    }

    @Operation(summary = "删除文件夹")
    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable String folderId) {
        String userId = getDefaultUserId();
        assetService.deleteFolder(folderId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== 角色操作 ====================

    @Operation(summary = "列出角色")
    @GetMapping("/characters")
    public ResponseEntity<ApiResponse<List<CharacterResponse>>> listCharacters(
            @RequestParam(required = false) String folderId) {
        String userId = getDefaultUserId();
        List<GlobalCharacter> characters = assetService.listCharacters(userId, folderId);
        List<CharacterResponse> response = characters.stream()
                .map(this::toCharacterResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/characters/{characterId}")
    public ResponseEntity<ApiResponse<CharacterResponse>> getCharacter(@PathVariable String characterId) {
        GlobalCharacter character = assetService.getCharacter(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found: " + characterId));
        List<GlobalCharacterAppearance> appearances = assetService.listAppearances(characterId);
        CharacterResponse response = toCharacterResponseWithAppearances(character, appearances);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "创建角色")
    @PostMapping("/characters")
    public ResponseEntity<ApiResponse<CharacterResponse>> createCharacter(@RequestBody CreateCharacterRequest request) {
        String userId = getDefaultUserId();
        GlobalCharacter character = new GlobalCharacter()
                .setUserId(userId)
                .setFolderId(request.getFolderId())
                .setName(request.getName())
                .setAliases(request.getAliases())
                .setProfileData(request.getProfileData())
                .setProfileConfirmed(request.getProfileConfirmed() != null && request.getProfileConfirmed() ? 1 : 0)
                .setVoiceId(request.getVoiceId())
                .setVoiceType(request.getVoiceType())
                .setCustomVoiceUrl(request.getCustomVoiceUrl())
                .setGlobalVoiceId(request.getGlobalVoiceId());
        character = assetService.createCharacter(character);
        return ResponseEntity.ok(ApiResponse.success(toCharacterResponse(character)));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/characters/{characterId}")
    public ResponseEntity<ApiResponse<CharacterResponse>> updateCharacter(
            @PathVariable String characterId,
            @RequestBody UpdateCharacterRequest request) {
        GlobalCharacter character = assetService.getCharacter(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found: " + characterId));
        character.setFolderId(request.getFolderId())
                .setName(request.getName())
                .setAliases(request.getAliases())
                .setProfileData(request.getProfileData())
                .setProfileConfirmed(request.getProfileConfirmed() != null && request.getProfileConfirmed() ? 1 : 0)
                .setVoiceId(request.getVoiceId())
                .setVoiceType(request.getVoiceType())
                .setCustomVoiceUrl(request.getCustomVoiceUrl())
                .setGlobalVoiceId(request.getGlobalVoiceId());
        character = assetService.updateCharacter(character);
        return ResponseEntity.ok(ApiResponse.success(toCharacterResponse(character)));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/characters/{characterId}")
    public ResponseEntity<ApiResponse<Void>> deleteCharacter(@PathVariable String characterId) {
        String userId = getDefaultUserId();
        assetService.deleteCharacter(characterId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    // ==================== 场景操作 ====================

    @Operation(summary = "列出场景")
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> listLocations(
            @RequestParam(required = false) String folderId) {
        String userId = getDefaultUserId();
        List<GlobalLocation> locations = assetService.listLocations(userId, folderId);
        List<LocationResponse> response = locations.stream()
                .map(this::toLocationResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取场景详情")
    @GetMapping("/locations/{locationId}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(@PathVariable String locationId) {
        GlobalLocation location = assetService.getLocation(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found: " + locationId));
        List<GlobalLocationImage> images = assetService.listLocationImages(locationId);
        LocationResponse response = toLocationResponseWithImages(location, images);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "创建场景")
    @PostMapping("/locations")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@RequestBody CreateLocationRequest request) {
        String userId = getDefaultUserId();
        GlobalLocation location = new GlobalLocation()
                .setUserId(userId)
                .setFolderId(request.getFolderId())
                .setName(request.getName())
                .setSummary(request.getSummary())
                .setArtStyle(request.getArtStyle());
        location = assetService.createLocation(location);
        return ResponseEntity.ok(ApiResponse.success(toLocationResponse(location)));
    }

    @Operation(summary = "更新场景")
    @PutMapping("/locations/{locationId}")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @PathVariable String locationId,
            @RequestBody UpdateLocationRequest request) {
        GlobalLocation location = assetService.getLocation(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found: " + locationId));
        location.setFolderId(request.getFolderId())
                .setName(request.getName())
                .setSummary(request.getSummary())
                .setArtStyle(request.getArtStyle());
        location = assetService.updateLocation(location);
        return ResponseEntity.ok(ApiResponse.success(toLocationResponse(location)));
    }

    @Operation(summary = "删除场景")
    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable String locationId) {
        String userId = getDefaultUserId();
        assetService.deleteLocation(locationId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== 场景图片操作 ====================

    @Operation(summary = "列出场景图片")
    @GetMapping("/locations/{locationId}/images")
    public ResponseEntity<ApiResponse<List<LocationImageResponse>>> listLocationImages(@PathVariable String locationId) {
        List<GlobalLocationImage> images = assetService.listLocationImages(locationId);
        List<LocationImageResponse> response = images.stream()
                .map(this::toLocationImageResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "添加场景图片")
    @PostMapping("/locations/{locationId}/images")
    public ResponseEntity<ApiResponse<LocationImageResponse>> createLocationImage(
            @PathVariable String locationId,
            @RequestBody CreateLocationImageRequest request) {
        GlobalLocationImage image = new GlobalLocationImage()
                .setLocationId(locationId)
                .setImageIndex(request.getImageIndex() != null ? request.getImageIndex() : 0)
                .setDescription(request.getDescription())
                .setImageUrl(request.getImageUrl())
                .setIsSelected(request.getIsSelected() != null && request.getIsSelected() ? 1 : 0);
        image = assetService.createLocationImage(image);
        return ResponseEntity.ok(ApiResponse.success(toLocationImageResponse(image)));
    }

    @Operation(summary = "删除场景图片")
    @DeleteMapping("/location-images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteLocationImage(@PathVariable String imageId) {
        assetService.deleteLocationImage(imageId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    // ==================== 转换方法 ====================

    private FolderResponse toFolderResponse(GlobalAssetFolder folder) {
        return new FolderResponse(
                folder.getFolderId(),
                folder.getName(),
                folder.getSortOrder(),
                folder.getCreatedAt()
        );
    }

    private CharacterResponse toCharacterResponse(GlobalCharacter character) {
        return new CharacterResponse(
                character.getCharacterId(),
                character.getFolderId(),
                character.getName(),
                character.getAliases(),
                character.getProfileData(),
                character.getProfileConfirmed() == 1,
                character.getVoiceId(),
                character.getVoiceType(),
                character.getGlobalVoiceId(),
                character.getCreatedAt(),
                null
        );
    }

    private CharacterResponse toCharacterResponseWithAppearances(GlobalCharacter character, List<GlobalCharacterAppearance> appearances) {
        List<AppearanceResponse> appearanceResponses = appearances.stream()
                .map(this::toAppearanceResponse)
                .collect(Collectors.toList());
        return new CharacterResponse(
                character.getCharacterId(),
                character.getFolderId(),
                character.getName(),
                character.getAliases(),
                character.getProfileData(),
                character.getProfileConfirmed() == 1,
                character.getVoiceId(),
                character.getVoiceType(),
                character.getGlobalVoiceId(),
                character.getCreatedAt(),
                appearanceResponses
        );
    }

    private AppearanceResponse toAppearanceResponse(GlobalCharacterAppearance appearance) {
        return new AppearanceResponse(
                appearance.getAppearanceId(),
                appearance.getAppearanceIndex(),
                appearance.getChangeReason(),
                appearance.getArtStyle(),
                appearance.getDescription(),
                appearance.getImageUrl(),
                appearance.getImageUrls(),
                appearance.getSelectedIndex(),
                appearance.getCreatedAt()
        );
    }

    private LocationResponse toLocationResponse(GlobalLocation location) {
        return new LocationResponse(
                location.getLocationId(),
                location.getFolderId(),
                location.getName(),
                location.getSummary(),
                location.getArtStyle(),
                location.getCreatedAt(),
                null
        );
    }

    private LocationResponse toLocationResponseWithImages(GlobalLocation location, List<GlobalLocationImage> images) {
        List<LocationImageResponse> imageResponses = images.stream()
                .map(this::toLocationImageResponse)
                .collect(Collectors.toList());
        return new LocationResponse(
                location.getLocationId(),
                location.getFolderId(),
                location.getName(),
                location.getSummary(),
                location.getArtStyle(),
                location.getCreatedAt(),
                imageResponses
        );
    }

    private LocationImageResponse toLocationImageResponse(GlobalLocationImage image) {
        return new LocationImageResponse(
                image.getImageId(),
                image.getImageIndex(),
                image.getDescription(),
                image.getImageUrl(),
                image.getIsSelected() == 1,
                image.getCreatedAt()
        );
    }

    private VoiceResponse toVoiceResponse(GlobalVoice voice) {
        return new VoiceResponse(
                voice.getVoiceId(),
                voice.getFolderId(),
                voice.getName(),
                voice.getDescription(),
                voice.getQwenVoiceId(),
                voice.getVoiceType(),
                voice.getCustomVoiceUrl(),
                voice.getVoicePrompt(),
                voice.getGender(),
                voice.getLanguage(),
                voice.getCreatedAt()
        );
    }
}