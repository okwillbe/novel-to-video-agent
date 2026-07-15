package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用成功响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {

    private boolean success;

    public static SuccessResponse success() {
        return new SuccessResponse(true);
    }
}
