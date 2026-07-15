package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppearanceResponse {
    private String appearanceId;
    private Integer appearanceIndex;
    private String changeReason;
    private String artStyle;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private Integer selectedIndex;
    private LocalDateTime createdAt;

    public static AppearanceResponse from(io.novel2video.agent.entity.GlobalCharacterAppearance appearance) {
        if (appearance == null) return null;
        AppearanceResponse response = new AppearanceResponse();
        response.setAppearanceId(appearance.getAppearanceId());
        response.setAppearanceIndex(appearance.getAppearanceIndex());
        response.setChangeReason(appearance.getChangeReason());
        response.setArtStyle(appearance.getArtStyle());
        response.setDescription(appearance.getDescription());
        response.setImageUrl(appearance.getImageUrl());
        response.setImageUrls(appearance.getImageUrls());
        response.setSelectedIndex(appearance.getSelectedIndex());
        response.setCreatedAt(appearance.getCreatedAt());
        return response;
    }
}