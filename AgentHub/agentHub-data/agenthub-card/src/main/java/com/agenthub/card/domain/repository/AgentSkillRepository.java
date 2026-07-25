package com.agenthub.card.domain.repository;

import com.agenthub.card.domain.entity.AgentSkill;

import java.util.List;

/**
 * Agent Skill 仓储接口
 */
public interface AgentSkillRepository {

    AgentSkill save(AgentSkill skill);
    AgentSkill update(AgentSkill skill);
    AgentSkill findById(Long id);
    List<AgentSkill> findByCardId(Long cardId);
    void deleteById(Long id);
    void deleteByCardId(Long cardId);
    Long nextId();
}
