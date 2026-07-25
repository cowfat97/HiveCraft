package com.agenthub.card.service.impl;

import com.agenthub.card.domain.entity.AgentCard;
import com.agenthub.card.domain.entity.AgentSkill;
import com.agenthub.common.dto.AgentCardDTO;
import com.agenthub.common.dto.AgentSkillDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Card/ Skill DTO 组装器
 */
public final class CardAssembler {

    private CardAssembler() {}

    public static AgentCardDTO toCardDTO(AgentCard card) {
        if (card == null) return null;
        return AgentCardDTO.builder()
                .id(card.getId())
                .agentId(card.getAgentId())
                .name(card.getName())
                .description(card.getDescription())
                .content(card.getContent())
                .summary(card.getSummary())
                .tags(card.getTags())
                .status(card.getStatus() != null ? card.getStatus().getCode() : null)
                .viewCount(card.getViewCount())
                .likeCount(card.getLikeCount())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .publishedAt(card.getPublishedAt())
                .build();
    }

    public static AgentSkillDTO toSkillDTO(AgentSkill skill) {
        if (skill == null) return null;
        return AgentSkillDTO.builder()
                .id(skill.getId())
                .cardId(skill.getCardId())
                .skillId(skill.getSkillId())
                .name(skill.getName())
                .description(skill.getDescription())
                .tags(skill.getTags())
                .examples(skill.getExamples())
                .inputModes(skill.getInputModes())
                .outputModes(skill.getOutputModes())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }

    public static List<AgentSkillDTO> toSkillDTOList(List<AgentSkill> skills) {
        return skills.stream().map(CardAssembler::toSkillDTO).collect(Collectors.toList());
    }
}
