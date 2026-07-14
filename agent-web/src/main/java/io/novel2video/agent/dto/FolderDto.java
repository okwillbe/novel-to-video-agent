package io.novel2video.agent.dto;

import io.novel2video.agent.entity.GlobalAssetFolder;
import lombok.Data;

/**
 * 文件夹 DTO
 */
@Data
public class FolderDto {

    private String id;
    private String name;

    public static FolderDto from(GlobalAssetFolder folder) {
        FolderDto dto = new FolderDto();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        return dto;
    }
}
