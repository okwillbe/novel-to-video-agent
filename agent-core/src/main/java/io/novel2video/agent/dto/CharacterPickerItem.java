package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色选择器项目
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterPickerItem {

    private String id;
    private String name;
    private String folderName;
    private String previewUrl;
    private Integer appearanceCount;
    private Boolean hasVoice;
}