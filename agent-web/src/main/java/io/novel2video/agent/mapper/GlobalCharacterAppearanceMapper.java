package io.novel2video.agent.mapper;

import io.novel2video.agent.entity.GlobalCharacterAppearance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色形象 Mapper
 */
@Mapper
public interface GlobalCharacterAppearanceMapper {

    /**
     * 根据角色ID获取所有形象
     */
    List<GlobalCharacterAppearance> findByCharacterId(@Param("characterId") String characterId);

    /**
     * 根据角色ID和形象序号获取形象
     */
    GlobalCharacterAppearance findByCharacterIdAndIndex(
            @Param("characterId") String characterId,
            @Param("appearanceIndex") Integer appearanceIndex);

    /**
     * 根据业务ID查询
     */
    GlobalCharacterAppearance selectByAppearanceId(@Param("appearanceId") String appearanceId);

    /**
     * 获取角色的最大形象序号
     */
    Integer getMaxAppearanceIndex(@Param("characterId") String characterId);

    /**
     * 插入形象
     */
    int insert(GlobalCharacterAppearance appearance);

    /**
     * 更新形象
     */
    int update(GlobalCharacterAppearance appearance);

    /**
     * 删除形象
     */
    int deleteByAppearanceId(@Param("appearanceId") String appearanceId);

    /**
     * 根据角色ID删除所有形象
     */
    int deleteByCharacterId(@Param("characterId") String characterId);

    /**
     * 更新选中的图片索引
     */
    int updateSelectedIndex(
            @Param("appearanceId") String appearanceId,
            @Param("selectedIndex") Integer selectedIndex);

    /**
     * 更新图片URLs（用于生成/修改后）
     */
    int updateImageUrls(
            @Param("appearanceId") String appearanceId,
            @Param("imageUrls") String imageUrls);

    /**
     * 保存撤销状态
     */
    int savePreviousState(
            @Param("appearanceId") String appearanceId,
            @Param("previousImageUrl") String previousImageUrl,
            @Param("previousImageMediaId") String previousImageMediaId,
            @Param("previousImageUrls") String previousImageUrls,
            @Param("previousDescriptions") String previousDescriptions);

    /**
     * 从撤销状态恢复
     */
    int restoreFromPreviousState(@Param("appearanceId") String appearanceId);
}