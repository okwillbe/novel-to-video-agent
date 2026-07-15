package io.novel2video.agent.service;

import io.novel2video.agent.entity.GlobalCharacter;
import io.novel2video.agent.entity.GlobalCharacterAppearance;
import io.novel2video.agent.exception.NotFoundException;
import io.novel2video.agent.mapper.GlobalCharacterAppearanceMapper;
import io.novel2video.agent.mapper.GlobalCharacterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 角色服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    private final GlobalCharacterMapper characterMapper;
    private final GlobalCharacterAppearanceMapper appearanceMapper;

    private static final String DEFAULT_USER_ID = "default-user";

    /**
     * 获取角色
     */
    public GlobalCharacter getCharacter(String characterId) {
        GlobalCharacter character = characterMapper.selectByCharacterId(characterId);
        if (character == null) {
            throw new NotFoundException("角色不存在: " + characterId);
        }
        return character;
    }

    /**
     * 获取用户所有角色
     */
    public List<GlobalCharacter> getCharacters(String userId) {
        return characterMapper.findByUserId(userId);
    }

    /**
     * 获取角色的所有形象
     */
    public List<GlobalCharacterAppearance> getAppearances(String characterId) {
        return appearanceMapper.findByCharacterId(characterId);
    }

    /**
     * 获取指定形象
     */
    public GlobalCharacterAppearance getAppearance(String characterId, Integer appearanceIndex) {
        GlobalCharacterAppearance appearance = appearanceMapper.findByCharacterIdAndIndex(characterId, appearanceIndex);
        if (appearance == null) {
            throw new NotFoundException("形象不存在");
        }
        return appearance;
    }

    /**
     * 创建角色
     */
    @Transactional
    public GlobalCharacter createCharacter(String name, String folderId) {
        GlobalCharacter character = new GlobalCharacter();
        character.setCharacterId(UUID.randomUUID().toString());
        character.setUserId(DEFAULT_USER_ID);
        character.setName(name);
        character.setFolderId(folderId);

        characterMapper.insert(character);
        log.info("Created character: {}", character.getCharacterId());

        // 创建默认形象
        GlobalCharacterAppearance appearance = new GlobalCharacterAppearance();
        appearance.setAppearanceId(UUID.randomUUID().toString());
        appearance.setCharacterId(character.getCharacterId());
        appearance.setAppearanceIndex(0);
        appearance.setChangeReason("初始形象");
        appearance.setSelectedIndex(0);
        appearanceMapper.insert(appearance);

        return character;
    }

    /**
     * 更新角色
     */
    @Transactional
    public void updateCharacter(GlobalCharacter character) {
        characterMapper.update(character);
        log.info("Updated character: {}", character.getCharacterId());
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteCharacter(String characterId) {
        // 删除所有形象
        appearanceMapper.deleteByCharacterId(characterId);
        // 删除角色
        characterMapper.deleteByCharacterId(characterId, DEFAULT_USER_ID);
        log.info("Deleted character: {}", characterId);
    }

    /**
     * 更新形象
     */
    @Transactional
    public void updateAppearance(GlobalCharacterAppearance appearance) {
        appearanceMapper.update(appearance);
        log.info("Updated appearance: {}", appearance.getAppearanceId());
    }
}