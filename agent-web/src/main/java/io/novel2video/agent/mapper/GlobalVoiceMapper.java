package io.novel2video.agent.mapper;

import io.novel2video.agent.entity.GlobalVoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 全局音色 Mapper
 */
@Mapper
public interface GlobalVoiceMapper {

    /**
     * 获取用户音色列表
     */
    List<GlobalVoice> findByUserId(@Param("userId") String userId);

    /**
     * 按文件夹筛选获取音色列表
     */
    List<GlobalVoice> findByUserIdAndFolderId(
            @Param("userId") String userId,
            @Param("folderId") String folderId);

    /**
     * 获取未分类音色
     */
    List<GlobalVoice> findUnclassified(@Param("userId") String userId);

    /**
     * 根据业务ID查询
     */
    GlobalVoice selectByVoiceId(@Param("voiceId") String voiceId);

    /**
     * 插入音色
     */
    int insert(GlobalVoice voice);

    /**
     * 更新音色
     */
    int update(GlobalVoice voice);

    /**
     * 根据业务ID删除
     */
    int deleteByVoiceId(@Param("voiceId") String voiceId, @Param("userId") String userId);

    /**
     * 检查百炼音色是否被其他音色引用
     */
    boolean existsByQwenVoiceIdAndNotVoiceId(
            @Param("qwenVoiceId") String qwenVoiceId,
            @Param("excludeVoiceId") String excludeVoiceId,
            @Param("userId") String userId);

    /**
     * 根据文件夹ID解绑音色
     */
    int detachFromFolder(@Param("folderId") String folderId);
}