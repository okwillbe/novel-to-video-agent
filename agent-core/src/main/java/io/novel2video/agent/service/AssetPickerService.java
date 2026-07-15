package io.novel2video.agent.service;

import io.novel2video.agent.dto.*;
import io.novel2video.agent.entity.GlobalCharacter;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.entity.GlobalLocation;
import io.novel2video.agent.entity.GlobalVoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 资产选择器服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetPickerService {

    private final CharacterService characterService;
    private final LocationService locationService;
    private final VoiceService voiceService;

    /**
     * 获取资产选择器数据
     */
    public AssetPickerResponse getPicker(String userId, String type) {
        AssetPickerResponse response = new AssetPickerResponse();

        if ("character".equals(type) || type == null) {
            response.setCharacters(getCharacterPickerItems(userId));
        }

        if ("location".equals(type) || type == null) {
            response.setLocations(getLocationPickerItems(userId));
        }

        if ("voice".equals(type) || type == null) {
            response.setVoices(getVoicePickerItems(userId));
        }

        return response;
    }

    /**
     * 获取角色选择器项目
     */
    private List<CharacterPickerItem> getCharacterPickerItems(String userId) {
        List<GlobalCharacter> characters = characterService.getCharacters(userId);

        return characters.stream()
            .map(c -> {
                List<GlobalCharacterAppearance> appearances = characterService.getAppearances(c.getCharacterId());
                String previewUrl = getPreviewUrl(appearances);

                return new CharacterPickerItem(
                    c.getCharacterId(),
                    c.getName(),
                    null, // TODO: 获取文件夹名
                    previewUrl,
                    appearances.size(),
                    c.getGlobalVoiceId() != null || c.getCustomVoiceUrl() != null
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取场景选择器项目
     */
    private List<LocationPickerItem> getLocationPickerItems(String userId) {
        List<GlobalLocation> locations = locationService.getLocations(userId);

        return locations.stream()
            .map(l -> new LocationPickerItem(
                l.getLocationId(),
                l.getName(),
                null, // TODO: 获取文件夹名
                l.getImageUrl()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 获取音色选择器项目
     */
    private List<VoicePickerItem> getVoicePickerItems(String userId) {
        List<GlobalVoice> voices = voiceService.listVoices(userId, null);

        return voices.stream()
            .map(VoicePickerItem::from)
            .collect(Collectors.toList());
    }

    /**
     * 获取预览图URL
     */
    private String getPreviewUrl(List<GlobalCharacterAppearance> appearances) {
        if (appearances == null || appearances.isEmpty()) {
            return null;
        }
        GlobalCharacterAppearance appearance = appearances.get(0);
        if (appearance.getImageUrls() == null || appearance.getImageUrls().isEmpty()) {
            return appearance.getImageUrl();
        }
        return appearance.getImageUrls().get(appearance.getSelectedIndex() != null ? appearance.getSelectedIndex() : 0);
    }
}