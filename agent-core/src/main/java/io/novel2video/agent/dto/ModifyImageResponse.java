package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片修改响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyImageResponse {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 状态: queued | processing | completed | failed
     */
    private String status;
}
