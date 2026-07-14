package io.novel2video.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新形象请求（所有字段可选）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppearanceRequest {

    /**
     * 形象说明
     */
    private String changeReason;

    /**
     * 描述词
     */
    private String description;

    /**
     * 艺术风格
     */
    private String artStyle;

    /**
     * 选中的图片索引
     */
    private Integer selectedIndex;
}
