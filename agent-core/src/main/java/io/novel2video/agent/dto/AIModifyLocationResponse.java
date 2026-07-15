package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 修改场景描述响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIModifyLocationResponse {

    /**
     * 修改后的描述词
     */
    private String prompt;
}
