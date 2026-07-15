package io.novel2video.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.novel2video.agent.entity.GlobalAssetFolder;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Global Asset Folder Mapper - 文件夹
 */
@Mapper
public interface GlobalAssetFolderMapper extends BaseMapper<GlobalAssetFolder> {

    List<GlobalAssetFolder> findByUserId(@Param("userId") String userId);

    GlobalAssetFolder findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    boolean existsByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    int deleteById(@Param("id") String id);

    List<FolderStatsDto> findFolderStatsByUserId(@Param("userId") String userId);

    int update(GlobalAssetFolder folder);

    @lombok.Data
    class FolderStatsDto {
        private String id;
        private String name;
        private Integer charCount;
        private Integer locCount;
        private Integer voiceCount;
    }
}