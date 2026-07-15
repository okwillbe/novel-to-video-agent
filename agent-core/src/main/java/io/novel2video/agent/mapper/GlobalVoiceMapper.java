package io.novel2video.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.novel2video.agent.entity.GlobalVoice;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Global Voice Mapper - 音色
 */
@Mapper
public interface GlobalVoiceMapper extends BaseMapper<GlobalVoice> {

    List<GlobalVoice> findByUserId(@Param("userId") String userId);

    List<GlobalVoice> findByUserIdAndFolderId(@Param("userId") String userId, @Param("folderId") String folderId);

    List<GlobalVoice> findUnclassified(@Param("userId") String userId);

    GlobalVoice selectByVoiceId(@Param("voiceId") String voiceId);

    int deleteByVoiceId(@Param("voiceId") String voiceId, @Param("userId") String userId);

    boolean existsByQwenVoiceIdAndNotVoiceId(@Param("qwenVoiceId") String qwenVoiceId,
                                             @Param("excludeVoiceId") String excludeVoiceId,
                                             @Param("userId") String userId);

    int detachFromFolder(@Param("folderId") String folderId);

    int update(GlobalVoice voice);
}