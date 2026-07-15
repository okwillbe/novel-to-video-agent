package io.novel2video.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Global Character Appearance Mapper - 角色形象（子资产）
 */
@Mapper
public interface GlobalCharacterAppearanceMapper extends BaseMapper<GlobalCharacterAppearance> {

    List<GlobalCharacterAppearance> findByCharacterId(@Param("characterId") String characterId);

    GlobalCharacterAppearance findByCharacterIdAndIndex(@Param("characterId") String characterId, @Param("appearanceIndex") Integer appearanceIndex);

    GlobalCharacterAppearance selectByAppearanceId(@Param("appearanceId") String appearanceId);

    Integer getMaxAppearanceIndex(@Param("characterId") String characterId);

    int updateSelectedIndex(@Param("appearanceId") String appearanceId, @Param("selectedIndex") Integer selectedIndex);

    int updateImageUrls(@Param("appearanceId") String appearanceId, @Param("imageUrls") String imageUrls);

    int savePreviousState(@Param("appearanceId") String appearanceId,
                          @Param("previousImageUrl") String previousImageUrl,
                          @Param("previousImageMediaId") String previousImageMediaId,
                          @Param("previousImageUrls") String previousImageUrls,
                          @Param("previousDescriptions") String previousDescriptions);

    int restoreFromPreviousState(@Param("appearanceId") String appearanceId);

    int deleteByAppearanceId(@Param("appearanceId") String appearanceId);

    int deleteByCharacterId(@Param("characterId") String characterId);

    int update(GlobalCharacterAppearance appearance);
}