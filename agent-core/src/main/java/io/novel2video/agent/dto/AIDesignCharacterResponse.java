package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 设计角色描述响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIDesignCharacterResponse {

    /**
     * 生成的角色描述词
     */
    private String prompt;
}
