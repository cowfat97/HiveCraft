package com.agenthub.infrastructure.repository.impl;

import com.agenthub.card.domain.entity.AgentSkill;
import com.agenthub.card.domain.repository.AgentSkillRepository;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.AgentSkillPO;
import com.agenthub.infrastructure.mapper.AgentSkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AgentSkillRepositoryImpl implements AgentSkillRepository {

    private final AgentSkillMapper skillMapper;
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== PO 转换 ====================

    private AgentSkill toEntity(AgentSkillPO po) {
        if (po == null) return null;
        return AgentSkill.builder()
                .id(po.getId())
                .cardId(po.getCardId())
                .skillId(po.getSkillId())
                .name(po.getName())
                .description(po.getDescription())
                .tags(po.getTags() != null ? Arrays.asList(po.getTags().split(",")) : null)
                .examples(po.getExamples() != null ? Arrays.asList(po.getExamples().split(",")) : null)
                .inputModes(po.getInputModes() != null ? Arrays.asList(po.getInputModes().split(",")) : null)
                .outputModes(po.getOutputModes() != null ? Arrays.asList(po.getOutputModes().split(",")) : null)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private AgentSkillPO toPO(AgentSkill skill) {
        if (skill == null) return null;
        return AgentSkillPO.builder()
                .id(skill.getId())
                .cardId(skill.getCardId())
                .skillId(skill.getSkillId())
                .name(skill.getName())
                .description(skill.getDescription())
                .tags(skill.getTags() != null ? String.join(",", skill.getTags()) : null)
                .examples(skill.getExamples() != null ? String.join(",", skill.getExamples()) : null)
                .inputModes(skill.getInputModes() != null ? String.join(",", skill.getInputModes()) : null)
                .outputModes(skill.getOutputModes() != null ? String.join(",", skill.getOutputModes()) : null)
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }

    // ==================== CRUD ====================

    @Override
    public AgentSkill save(AgentSkill skill) {
        AgentSkillPO po = toPO(skill);
        if (po.getCreatedAt() == null) po.setCreatedAt(LocalDateTime.now());
        if (po.getUpdatedAt() == null) po.setUpdatedAt(LocalDateTime.now());
        skillMapper.insert(po);
        return toEntity(po);
    }

    @Override
    public AgentSkill update(AgentSkill skill) {
        AgentSkillPO po = toPO(skill);
        po.setUpdatedAt(LocalDateTime.now());
        skillMapper.update(po);
        return toEntity(po);
    }

    @Override
    public AgentSkill findById(Long id) {
        return toEntity(skillMapper.selectById(id));
    }

    @Override
    public List<AgentSkill> findByCardId(Long cardId) {
        return skillMapper.selectByCardId(cardId).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        skillMapper.deleteById(id);
    }

    @Override
    public void deleteByCardId(Long cardId) {
        skillMapper.deleteByCardId(cardId);
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }
}
