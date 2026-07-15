package io.novel2video.agent.dto;

import lombok.Data;

import java.util.List;

/**
 * 资产选择器响应
 */
@Data
public class AssetPickerResponse {

    private List<CharacterPickerItem> characters;
    private List<LocationPickerItem> locations;
    private List<VoicePickerItem> voices;
}