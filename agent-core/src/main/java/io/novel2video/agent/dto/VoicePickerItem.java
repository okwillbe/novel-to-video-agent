package io.novel2video.agent.dto;

import io.novel2video.agent.entity.GlobalVoice;
import lombok.Data;

/**
 * 音色选择器项目
 */
@Data
public class VoicePickerItem {

    private String voiceId;
    private String name;
    private String description;
    private String customVoiceUrl;
    private String qwenVoiceId;
    private String voiceType;
    private String gender;
    private String language;

    public static VoicePickerItem from(GlobalVoice voice) {
        VoicePickerItem item = new VoicePickerItem();
        item.setVoiceId(voice.getVoiceId());
        item.setName(voice.getName());
        item.setDescription(voice.getDescription());
        item.setCustomVoiceUrl(voice.getCustomVoiceUrl());
        item.setQwenVoiceId(voice.getQwenVoiceId());
        item.setVoiceType(voice.getVoiceType());
        item.setGender(voice.getGender());
        item.setLanguage(voice.getLanguage());
        return item;
    }
}
