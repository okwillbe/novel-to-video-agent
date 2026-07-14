package io.novel2video.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.novel2video.agent.entity.*;
import io.novel2video.agent.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Asset Service - 统一资产管理服务
 *
 * 提供文件夹、角色、场景、音色的 CRUD 操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final GlobalAssetFolderMapper folderMapper;
    private final GlobalCharacterMapper characterMapper;
    private final GlobalCharacterAppearanceMapper appearanceMapper;
    private final GlobalLocationMapper locationMapper;
    private final GlobalLocationImageMapper locationImageMapper;
    private final GlobalVoiceMapper voiceMapper;

    // ==================== 文件夹操作 ====================

    public List<GlobalAssetFolder> listFolders(String userId) {
        LambdaQueryWrapper<GlobalAssetFolder> wrapper = new LambdaQueryWrapper<GlobalAssetFolder>()
                .eq(GlobalAssetFolder::getUserId, userId)
                .orderByAsc(GlobalAssetFolder::getSortOrder);
        return folderMapper.selectList(wrapper);
    }

    public GlobalAssetFolder createFolder(GlobalAssetFolder folder) {
        if (folder.getFolderId() == null) {
            folder.setFolderId(UUID.randomUUID().toString());
        }
        folderMapper.insert(folder);
        log.info("Created folder: {} for user: {}", folder.getFolderId(), folder.getUserId());
        return folder;
    }

    public void deleteFolder(String folderId, String userId) {
        LambdaQueryWrapper<GlobalAssetFolder> wrapper = new LambdaQueryWrapper<GlobalAssetFolder>()
                .eq(GlobalAssetFolder::getFolderId, folderId)
                .eq(GlobalAssetFolder::getUserId, userId);
        folderMapper.delete(wrapper);
        log.info("Deleted folder: {}", folderId);
    }

    // ==================== 角色操作 ====================

    public List<GlobalCharacter> listCharacters(String userId, String folderId) {
        LambdaQueryWrapper<GlobalCharacter> wrapper = new LambdaQueryWrapper<GlobalCharacter>()
                .eq(GlobalCharacter::getUserId, userId);
        if (folderId != null) {
            wrapper.eq(GlobalCharacter::getFolderId, folderId);
        }
        wrapper.orderByDesc(GlobalCharacter::getCreatedAt);
        return characterMapper.selectList(wrapper);
    }

    public Optional<GlobalCharacter> getCharacter(String characterId) {
        LambdaQueryWrapper<GlobalCharacter> wrapper = new LambdaQueryWrapper<GlobalCharacter>()
                .eq(GlobalCharacter::getCharacterId, characterId);
        return Optional.ofNullable(characterMapper.selectOne(wrapper));
    }

    @Transactional
    public GlobalCharacter createCharacter(GlobalCharacter character) {
        if (character.getCharacterId() == null) {
            character.setCharacterId(UUID.randomUUID().toString());
        }
        characterMapper.insert(character);
        log.info("Created character: {} for user: {}", character.getCharacterId(), character.getUserId());
        return character;
    }

    public GlobalCharacter updateCharacter(GlobalCharacter character) {
        characterMapper.updateById(character);
        log.info("Updated character: {}", character.getCharacterId());
        return character;
    }

    @Transactional
    public void deleteCharacter(String characterId, String userId) {
        // 先删除所有形象
        LambdaQueryWrapper<GlobalCharacterAppearance> appearanceWrapper = new LambdaQueryWrapper<GlobalCharacterAppearance>()
                .eq(GlobalCharacterAppearance::getCharacterId, characterId);
        appearanceMapper.delete(appearanceWrapper);

        // 再删除角色
        LambdaQueryWrapper<GlobalCharacter> wrapper = new LambdaQueryWrapper<GlobalCharacter>()
                .eq(GlobalCharacter::getCharacterId, characterId)
                .eq(GlobalCharacter::getUserId, userId);
        characterMapper.delete(wrapper);
        log.info("Deleted character: {}", characterId);
    }

    // ==================== 角色形象操作 ====================

    public List<GlobalCharacterAppearance> listAppearances(String characterId) {
        LambdaQueryWrapper<GlobalCharacterAppearance> wrapper = new LambdaQueryWrapper<GlobalCharacterAppearance>()
                .eq(GlobalCharacterAppearance::getCharacterId, characterId)
                .orderByAsc(GlobalCharacterAppearance::getAppearanceIndex);
        return appearanceMapper.selectList(wrapper);
    }

    public GlobalCharacterAppearance createAppearance(GlobalCharacterAppearance appearance) {
        if (appearance.getAppearanceId() == null) {
            appearance.setAppearanceId(UUID.randomUUID().toString());
        }
        appearanceMapper.insert(appearance);
        log.info("Created appearance: {} for character: {}", appearance.getAppearanceId(), appearance.getCharacterId());
        return appearance;
    }

    public void deleteAppearance(String appearanceId) {
        appearanceMapper.deleteById(appearanceId);
        log.info("Deleted appearance: {}", appearanceId);
    }

    // ==================== 场景操作 ====================

    public List<GlobalLocation> listLocations(String userId, String folderId) {
        LambdaQueryWrapper<GlobalLocation> wrapper = new LambdaQueryWrapper<GlobalLocation>()
                .eq(GlobalLocation::getUserId, userId);
        if (folderId != null) {
            wrapper.eq(GlobalLocation::getFolderId, folderId);
        }
        wrapper.orderByDesc(GlobalLocation::getCreatedAt);
        return locationMapper.selectList(wrapper);
    }

    public Optional<GlobalLocation> getLocation(String locationId) {
        LambdaQueryWrapper<GlobalLocation> wrapper = new LambdaQueryWrapper<GlobalLocation>()
                .eq(GlobalLocation::getLocationId, locationId);
        return Optional.ofNullable(locationMapper.selectOne(wrapper));
    }

    public GlobalLocation createLocation(GlobalLocation location) {
        if (location.getLocationId() == null) {
            location.setLocationId(UUID.randomUUID().toString());
        }
        locationMapper.insert(location);
        log.info("Created location: {} for user: {}", location.getLocationId(), location.getUserId());
        return location;
    }

    public GlobalLocation updateLocation(GlobalLocation location) {
        locationMapper.updateById(location);
        log.info("Updated location: {}", location.getLocationId());
        return location;
    }

    @Transactional
    public void deleteLocation(String locationId, String userId) {
        // 先删除所有图片
        LambdaQueryWrapper<GlobalLocationImage> imageWrapper = new LambdaQueryWrapper<GlobalLocationImage>()
                .eq(GlobalLocationImage::getLocationId, locationId);
        locationImageMapper.delete(imageWrapper);

        // 再删除场景
        LambdaQueryWrapper<GlobalLocation> wrapper = new LambdaQueryWrapper<GlobalLocation>()
                .eq(GlobalLocation::getLocationId, locationId)
                .eq(GlobalLocation::getUserId, userId);
        locationMapper.delete(wrapper);
        log.info("Deleted location: {}", locationId);
    }

    // ==================== 场景图片操作 ====================

    public List<GlobalLocationImage> listLocationImages(String locationId) {
        LambdaQueryWrapper<GlobalLocationImage> wrapper = new LambdaQueryWrapper<GlobalLocationImage>()
                .eq(GlobalLocationImage::getLocationId, locationId)
                .orderByAsc(GlobalLocationImage::getImageIndex);
        return locationImageMapper.selectList(wrapper);
    }

    public GlobalLocationImage createLocationImage(GlobalLocationImage image) {
        if (image.getImageId() == null) {
            image.setImageId(UUID.randomUUID().toString());
        }
        locationImageMapper.insert(image);
        log.info("Created location image: {} for location: {}", image.getImageId(), image.getLocationId());
        return image;
    }

    public void deleteLocationImage(String imageId) {
        locationImageMapper.deleteById(imageId);
        log.info("Deleted location image: {}", imageId);
    }

    // ==================== 音色操作 ====================

    public List<GlobalVoice> listVoices(String userId, String folderId) {
        LambdaQueryWrapper<GlobalVoice> wrapper = new LambdaQueryWrapper<GlobalVoice>()
                .eq(GlobalVoice::getUserId, userId);
        if (folderId != null) {
            wrapper.eq(GlobalVoice::getFolderId, folderId);
        }
        wrapper.orderByDesc(GlobalVoice::getCreatedAt);
        return voiceMapper.selectList(wrapper);
    }

    public Optional<GlobalVoice> getVoice(String voiceId) {
        LambdaQueryWrapper<GlobalVoice> wrapper = new LambdaQueryWrapper<GlobalVoice>()
                .eq(GlobalVoice::getVoiceId, voiceId);
        return Optional.ofNullable(voiceMapper.selectOne(wrapper));
    }

    public GlobalVoice createVoice(GlobalVoice voice) {
        if (voice.getVoiceId() == null) {
            voice.setVoiceId(UUID.randomUUID().toString());
        }
        voiceMapper.insert(voice);
        log.info("Created voice: {} for user: {}", voice.getVoiceId(), voice.getUserId());
        return voice;
    }

    public GlobalVoice updateVoice(GlobalVoice voice) {
        voiceMapper.updateById(voice);
        log.info("Updated voice: {}", voice.getVoiceId());
        return voice;
    }

    public void deleteVoice(String voiceId, String userId) {
        LambdaQueryWrapper<GlobalVoice> wrapper = new LambdaQueryWrapper<GlobalVoice>()
                .eq(GlobalVoice::getVoiceId, voiceId)
                .eq(GlobalVoice::getUserId, userId);
        voiceMapper.delete(wrapper);
        log.info("Deleted voice: {}", voiceId);
    }
}