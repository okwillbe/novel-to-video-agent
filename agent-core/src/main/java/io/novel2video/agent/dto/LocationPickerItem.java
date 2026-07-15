package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场景选择器项目
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationPickerItem {

    private String id;
    private String name;
    private String folderName;
    private String previewUrl;
}