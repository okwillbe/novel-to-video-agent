package io.novel2video.agent.mapper;

import io.novel2video.agent.entity.GlobalAssetFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 全局资产文件夹 Mapper
 */
@Mapper
public interface GlobalAssetFolderMapper {

    /**
     * 根据用户ID获取文件夹列表
     */
    List<GlobalAssetFolder> findByUserId(@Param("userId") String userId);

    /**
     * 根据ID和用户ID获取文件夹
     */
    GlobalAssetFolder findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    /**
     * 检查文件夹是否存在
     */
    boolean existsByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    /**
     * 插入文件夹
     */
    int insert(GlobalAssetFolder folder);

    /**
     * 更新文件夹
     */
    int update(GlobalAssetFolder folder);

    /**
     * 删除文件夹
     */
    int deleteById(@Param("id") String id);

    /**
     * 获取文件夹统计信息（资产数量）
     */
    List<FolderStatsDto> findFolderStatsByUserId(@Param("userId") String userId);

    /**
     * 文件夹统计 DTO
     */
    @lombok.Data
    class FolderStatsDto {
        private String id;
        private String name;
        private Integer charCount;
        private Integer locCount;
        private Integer voiceCount;
    }
}