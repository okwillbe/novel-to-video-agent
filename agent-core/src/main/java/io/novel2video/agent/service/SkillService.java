package io.novel2video.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.novel2video.agent.entity.Skill;
import io.novel2video.agent.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Skill Service - CRUD and search operations for the Skills Repository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;

    public Skill createSkill(Skill skill) {
        skillMapper.insert(skill);
        log.info("Created skill: {} v{}", skill.getSkillId(), skill.getVersion());
        return skill;
    }

    public Optional<Skill> findBySkillIdAndVersion(String skillId, String version) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
                .eq(Skill::getSkillId, skillId)
                .eq(Skill::getVersion, version);
        return Optional.ofNullable(skillMapper.selectOne(wrapper));
    }

    public List<Skill> findByCategory(String category) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
                .eq(Skill::getCategory, category)
                .eq(Skill::getStatus, 1)
                .orderByDesc(Skill::getUseCount);
        return skillMapper.selectList(wrapper);
    }

    public List<Skill> searchSkills(String keyword) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
                .eq(Skill::getStatus, 1)
                .and(w -> w
                        .like(Skill::getName, keyword)
                        .or().like(Skill::getDescription, keyword)
                        .or().like(Skill::getTags, keyword))
                .orderByDesc(Skill::getUseCount);
        return skillMapper.selectList(wrapper);
    }

    public List<Skill> findPopularSkills(int limit) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
                .eq(Skill::getStatus, 1)
                .eq(Skill::getIsPublic, 1)
                .orderByDesc(Skill::getUseCount)
                .last("LIMIT " + limit);
        return skillMapper.selectList(wrapper);
    }

    public List<Skill> findAllEnabled() {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
                .eq(Skill::getStatus, 1)
                .eq(Skill::getIsPublic, 1)
                .orderByAsc(Skill::getCategory)
                .orderByDesc(Skill::getUseCount);
        return skillMapper.selectList(wrapper);
    }

    public Skill updateSkill(Skill skill) {
        skillMapper.updateById(skill);
        log.info("Updated skill: {} v{}", skill.getSkillId(), skill.getVersion());
        return skill;
    }

    public void incrementUseCount(String skillId, String version, boolean success) {
        findBySkillIdAndVersion(skillId, version).ifPresent(skill -> {
            skill.setUseCount(skill.getUseCount() + 1);
            if (success) {
                skill.setSuccessCount(skill.getSuccessCount() + 1);
            }
            skillMapper.updateById(skill);
        });
    }
}
