package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 设计场景描述响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIDesignLocationResponse {

    /**
     * 生成的场景描述词
     */
    private String prompt;
}
