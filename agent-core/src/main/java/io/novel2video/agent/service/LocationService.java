package io.novel2video.agent.service;

import io.novel2video.agent.entity.GlobalLocation;
import io.novel2video.agent.exception.NotFoundException;
import io.novel2video.agent.mapper.GlobalLocationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 场景服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final GlobalLocationMapper locationMapper;

    private static final String DEFAULT_USER_ID = "default-user";

    /**
     * 获取场景
     */
    public GlobalLocation getLocation(String locationId) {
        GlobalLocation location = locationMapper.selectByLocationId(locationId);
        if (location == null) {
            throw new NotFoundException("场景不存在: " + locationId);
        }
        return location;
    }

    /**
     * 获取用户所有场景
     */
    public List<GlobalLocation> getLocations(String userId) {
        return locationMapper.findByUserId(userId);
    }

    /**
     * 创建场景
     */
    @Transactional
    public GlobalLocation createLocation(String name, String folderId) {
        GlobalLocation location = new GlobalLocation();
        location.setLocationId(UUID.randomUUID().toString());
        location.setUserId(DEFAULT_USER_ID);
        location.setName(name);
        location.setFolderId(folderId);

        locationMapper.insert(location);
        log.info("Created location: {}", location.getLocationId());

        return location;
    }

    /**
     * 更新场景
     */
    @Transactional
    public void updateLocation(GlobalLocation location) {
        locationMapper.update(location);
        log.info("Updated location: {}", location.getLocationId());
    }

    /**
     * 删除场景
     */
    @Transactional
    public void deleteLocation(String locationId) {
        locationMapper.deleteByLocationId(locationId, DEFAULT_USER_ID);
        log.info("Deleted location: {}", locationId);
    }
}