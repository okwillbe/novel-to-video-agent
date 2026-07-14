package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ==================== 文件夹 DTO ====================

@Data
@AllArgsConstructor
class FolderResponse {
    private String folderId;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}

@Data
class CreateFolderRequest {
    private String name;
    private Integer sortOrder;
}

// ==================== 角色 DTO ====================

@Data
@AllArgsConstructor
class CharacterResponse {
    private String characterId;
    private String folderId;
    private String name;
    private List<String> aliases;
    private Object profileData;
    private Boolean profileConfirmed;
    private String voiceId;
    private String voiceType;
    private String globalVoiceId;
    private LocalDateTime createdAt;
    private List<AppearanceResponse> appearances;
}

@Data
class CreateCharacterRequest {
    private String folderId;
    private String name;
    private List<String> aliases;
    private Object profileData;
    private Boolean profileConfirmed;
    private String voiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String globalVoiceId;
}

@Data
class UpdateCharacterRequest {
    private String folderId;
    private String name;
    private List<String> aliases;
    private Object profileData;
    private Boolean profileConfirmed;
    private String voiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String globalVoiceId;
}

// ==================== 角色形象 DTO ====================

@Data
@AllArgsConstructor
class AppearanceResponse {
    private String appearanceId;
    private Integer appearanceIndex;
    private String changeReason;
    private String artStyle;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private Integer selectedIndex;
    private LocalDateTime createdAt;
}

@Data
class CreateAppearanceRequest {
    private Integer appearanceIndex;
    private String changeReason;
    private String artStyle;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private Integer selectedIndex;
}

// ==================== 场景 DTO ====================

@Data
@AllArgsConstructor
class LocationResponse {
    private String locationId;
    private String folderId;
    private String name;
    private String summary;
    private String artStyle;
    private LocalDateTime createdAt;
    private List<LocationImageResponse> images;
}

@Data
class CreateLocationRequest {
    private String folderId;
    private String name;
    private String summary;
    private String artStyle;
}

@Data
class UpdateLocationRequest {
    private String folderId;
    private String name;
    private String summary;
    private String artStyle;
}

// ==================== 场景图片 DTO ====================

@Data
@AllArgsConstructor
class LocationImageResponse {
    private String imageId;
    private Integer imageIndex;
    private String description;
    private String imageUrl;
    private Boolean isSelected;
    private LocalDateTime createdAt;
}

@Data
class CreateLocationImageRequest {
    private Integer imageIndex;
    private String description;
    private String imageUrl;
    private Boolean isSelected;
}

// ==================== 音色 DTO ====================

@Data
@AllArgsConstructor
class VoiceResponse {
    private String voiceId;
    private String folderId;
    private String name;
    private String description;
    private String qwenVoiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String voicePrompt;
    private String gender;
    private String language;
    private LocalDateTime createdAt;
}

@Data
class CreateVoiceRequest {
    private String folderId;
    private String name;
    private String description;
    private String qwenVoiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String voicePrompt;
    private String gender;
    private String language;
}

@Data
class UpdateVoiceRequest {
    private String folderId;
    private String name;
    private String description;
    private String qwenVoiceId;
    private String voiceType;
    private String customVoiceUrl;
    private String voicePrompt;
    private String gender;
    private String language;
}