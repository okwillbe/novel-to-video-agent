package io.novel2video.agent.dto;

import io.novel2video.agent.entity.GlobalAssetFolder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件夹列表响应
 */
@Data
public class FolderListResponse {

    private List<FolderDto> folders;

    public static FolderListResponse from(List<GlobalAssetFolder> folderList) {
        FolderListResponse response = new FolderListResponse();
        response.setFolders(folderList.stream()
                .map(FolderDto::from)
                .collect(Collectors.toList()));
        return response;
    }
}
