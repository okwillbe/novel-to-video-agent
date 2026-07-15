package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {
    private String folderId;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
