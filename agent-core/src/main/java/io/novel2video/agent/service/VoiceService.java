package io.novel2video.agent.service;

import io.novel2video.agent.dto.CreateVoiceRequest;
import io.novel2video.agent.dto.UpdateVoiceRequest;
import io.novel2video.agent.dto.VoiceDesignRequest;
import io.novel2video.agent.dto.VoiceDesignResponse;
import io.novel2video.agent.entity.GlobalVoice;
import io.novel2video.agent.exception.NotFoundException;
import io.novel2video.agent.mapper.GlobalVoiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 音色服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceService {

    private final GlobalVoiceMapper voiceMapper;

    /**
     * 获取音色列表
     */
    public List<GlobalVoice> listVoices(String userId, String folderId) {
        if (folderId == null) {
            return voiceMapper.findByUserId(userId);
        } else if ("null".equals(folderId)) {
            return voiceMapper.findUnclassified(userId);
        } else {
            return voiceMapper.findByUserIdAndFolderId(userId, folderId);
        }
    }

    /**
     * 获取单个音色
     */
    public GlobalVoice getVoice(String voiceId) {
        GlobalVoice voice = voiceMapper.selectByVoiceId(voiceId);
        if (voice == null) {
            throw new NotFoundException("音色不存在: " + voiceId);
        }
        return voice;
    }

    /**
     * 创建音色
     */
    public GlobalVoice createVoice(String userId, CreateVoiceRequest request) {
        GlobalVoice voice = new GlobalVoice();
        voice.setVoiceId(UUID.randomUUID().toString());
        voice.setUserId(userId);
        voice.setName(request.getName());
        voice.setDescription(request.getDescription());
        voice.setFolderId(request.getFolderId());
        voice.setQwenVoiceId(request.getQwenVoiceId());
        voice.setVoiceType(request.getVoiceType() != null ? request.getVoiceType() : "qwen-designed");
        voice.setCustomVoiceUrl(request.getCustomVoiceUrl());
        voice.setVoicePrompt(request.getVoicePrompt());
        voice.setGender(request.getGender());
        voice.setLanguage(request.getLanguage() != null ? request.getLanguage() : "zh");

        voiceMapper.insert(voice);
        log.info("Created voice: {}", voice.getVoiceId());
        return voice;
    }

    /**
     * 更新音色
     */
    public GlobalVoice updateVoice(String voiceId, UpdateVoiceRequest request) {
        GlobalVoice voice = voiceMapper.selectByVoiceId(voiceId);
        if (voice == null) {
            throw new NotFoundException("音色不存在: " + voiceId);
        }

        if (request.getName() != null) {
            voice.setName(request.getName());
        }
        if (request.getDescription() != null) {
            voice.setDescription(request.getDescription());
        }
        if (request.getFolderId() != null) {
            voice.setFolderId("null".equals(request.getFolderId()) ? null : request.getFolderId());
        }
        if (request.getQwenVoiceId() != null) {
            voice.setQwenVoiceId(request.getQwenVoiceId());
        }
        if (request.getVoiceType() != null) {
            voice.setVoiceType(request.getVoiceType());
        }
        if (request.getCustomVoiceUrl() != null) {
            voice.setCustomVoiceUrl(request.getCustomVoiceUrl());
        }
        if (request.getGender() != null) {
            voice.setGender(request.getGender());
        }
        if (request.getLanguage() != null) {
            voice.setLanguage(request.getLanguage());
        }

        voiceMapper.update(voice);
        log.info("Updated voice: {}", voiceId);
        return voice;
    }

    /**
     * 删除音色
     */
    @Transactional
    public void deleteVoice(String voiceId, String userId) {
        GlobalVoice voice = voiceMapper.selectByVoiceId(voiceId);
        if (voice == null) {
            return;
        }

        // 如果是百炼音色，检查是否需要清理
        if ("qwen-designed".equals(voice.getVoiceType()) && voice.getQwenVoiceId() != null) {
            boolean hasOtherRef = voiceMapper.existsByQwenVoiceIdAndNotVoiceId(
                    voice.getQwenVoiceId(), voiceId, userId);
            if (!hasOtherRef) {
                // TODO: 调用百炼 API 清理音色
                log.info("Should cleanup qwen voice: {}", voice.getQwenVoiceId());
            }
        }

        // TODO: 清空引用此音色的角色的 globalVoiceId

        // 删除音色记录
        voiceMapper.deleteByVoiceId(voiceId, userId);
        log.info("Deleted voice: {}", voiceId);
    }

    /**
     * AI 设计音色
     */
    public VoiceDesignResponse designVoice(VoiceDesignRequest request) {
        // TODO: 调用 qwen-tts-vd API
        log.info("Design voice for speaker: {}", request.getSpeaker());
        // 模拟返回
        return new VoiceDesignResponse(
                "qwen-voice-" + UUID.randomUUID().toString().substring(0, 8),
                null
        );
    }

    /**
     * 获取音色选择器列表
     */
    public List<GlobalVoice> getVoicePicker(String userId) {
        return voiceMapper.findByUserId(userId);
    }
}