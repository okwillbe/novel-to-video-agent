package io.novel2video.agent.dto;

import io.novel2video.agent.entity.GlobalCharacterAppearance;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 形象响应 DTO
 */
@Data
public class AppearanceResponse {

    private String appearanceId;
    private String characterId;
    private Integer appearanceIndex;
    private String changeReason;
    private String artStyle;
    private String description;
    private List<String> descriptions;
    private String descriptionSource;
    private String imageUrl;
    private List<String> imageUrls;
    private Integer selectedIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AppearanceResponse from(GlobalCharacterAppearance appearance) {
        AppearanceResponse response = new AppearanceResponse();
        response.setAppearanceId(appearance.getAppearanceId());
        response.setCharacterId(appearance.getCharacterId());
        response.setAppearanceIndex(appearance.getAppearanceIndex());
        response.setChangeReason(appearance.getChangeReason());
        response.setArtStyle(appearance.getArtStyle());
        response.setDescription(appearance.getDescription());
        response.setDescriptions(appearance.getDescriptions());
        response.setDescriptionSource(appearance.getDescriptionSource());
        response.setImageUrl(appearance.getImageUrl());
        response.setImageUrls(appearance.getImageUrls());
        response.setSelectedIndex(appearance.getSelectedIndex());
        response.setCreatedAt(appearance.getCreatedAt());
        response.setUpdatedAt(appearance.getUpdatedAt());
        return response;
    }
}
