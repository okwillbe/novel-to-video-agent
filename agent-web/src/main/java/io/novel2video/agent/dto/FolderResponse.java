package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件夹操作响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {

    private boolean success;
    private FolderDto folder;

    public static FolderResponse success(FolderDto folder) {
        return new FolderResponse(true, folder);
    }
}
