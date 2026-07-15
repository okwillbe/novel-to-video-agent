package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 参考图生成角色响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceToCharacterResponse {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 状态
     */
    private String status;

    /**
     * 生成的图片URL列表
     */
    private List<String> imageUrls;

    /**
     * 提取的描述词
     */
    private String description;
}