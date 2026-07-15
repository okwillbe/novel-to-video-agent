package io.novel2video.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.novel2video.agent.entity.GlobalCharacter;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Global Character Mapper - 角色
 */
@Mapper
public interface GlobalCharacterMapper extends BaseMapper<GlobalCharacter> {

    GlobalCharacter selectByCharacterId(@Param("characterId") String characterId);

    List<GlobalCharacter> findByUserId(@Param("userId") String userId);

    List<GlobalCharacter> findByFolderId(@Param("folderId") String folderId);

    int deleteByCharacterId(@Param("characterId") String characterId, @Param("userId") String userId);

    int detachFromFolder(@Param("folderId") String folderId);

    int update(GlobalCharacter character);
}