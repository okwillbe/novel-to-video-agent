package io.novel2video.agent.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String locationId;
    private String folderId;
    private String name;
    private String summary;
    private String artStyle;
    private LocalDateTime createdAt;
    private List<LocationImageResponse> images;
}
