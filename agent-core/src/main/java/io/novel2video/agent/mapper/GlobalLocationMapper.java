package io.novel2video.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.novel2video.agent.entity.GlobalLocation;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Global Location Mapper - 场景
 */
@Mapper
public interface GlobalLocationMapper extends BaseMapper<GlobalLocation> {

    GlobalLocation selectByLocationId(@Param("locationId") String locationId);

    List<GlobalLocation> findByUserId(@Param("userId") String userId);

    List<GlobalLocation> findByFolderId(@Param("folderId") String folderId);

    int deleteByLocationId(@Param("locationId") String locationId, @Param("userId") String userId);

    int detachFromFolder(@Param("folderId") String folderId);

    int update(GlobalLocation location);
}