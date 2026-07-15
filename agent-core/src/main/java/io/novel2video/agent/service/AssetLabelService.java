package io.novel2video.agent.service;

import io.novel2video.agent.dto.UpdateAssetLabelRequest;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产标签服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetLabelService {

    private final CharacterService characterService;
    private final LocationService locationService;

    /**
     * 更新资产标签
     */
    @Transactional
    public String updateAssetLabel(String userId, UpdateAssetLabelRequest request) {
        if ("character".equals(request.getType())) {
            return updateCharacterLabel(request);
        } else {
            return updateLocationLabel(request);
        }
    }

    /**
     * 更新角色标签
     */
    private String updateCharacterLabel(UpdateAssetLabelRequest request) {
        GlobalCharacterAppearance appearance = characterService.getAppearance(
            request.getId(),
            request.getAppearanceIndex() != null ? request.getAppearanceIndex() : 0
        );

        List<String> newUrls = new ArrayList<>();
        if (appearance.getImageUrls() != null) {
            for (String oldUrl : appearance.getImageUrls()) {
                // TODO: 调用 MinioService 更新标签
                String newUrl = oldUrl.replace(".jpg", "_" + request.getNewName() + ".jpg");
                newUrls.add(newUrl);
            }
        }

        // 更新数据库
        appearance.setImageUrls(newUrls);
        characterService.updateAppearance(appearance);

        log.info("Updated character label: {} -> {}", request.getId(), request.getNewName());

        return newUrls.isEmpty() ? null : newUrls.get(0);
    }

    /**
     * 更新场景标签
     */
    private String updateLocationLabel(UpdateAssetLabelRequest request) {
        // TODO: 实现场景标签更新
        log.info("Updating location label: {} -> {}", request.getId(), request.getNewName());
        return null;
    }
}