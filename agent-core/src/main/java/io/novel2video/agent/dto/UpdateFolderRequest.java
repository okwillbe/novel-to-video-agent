package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新文件夹请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolderRequest {

    /**
     * 文件夹名称
     */
    private String name;
}
