package com.agenthub.card.service.impl;

import com.agenthub.card.domain.entity.AgentSkill;
import com.agenthub.card.domain.repository.AgentSkillRepository;
import com.agenthub.card.dto.AgentSkillCreateRequest;
import com.agenthub.card.dto.AgentSkillUpdateRequest;
import com.agenthub.card.service.AgentSkillService;
import com.agenthub.common.dto.AgentSkillDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentSkillServiceImpl implements AgentSkillService {

    private final AgentSkillRepository skillRepository;

    @Override
    @Transactional
    public AgentSkillDTO create(Long cardId, AgentSkillCreateRequest request) {
        Long id = skillRepository.nextId();

        AgentSkill skill = AgentSkill.create(
                id, cardId,
                request.getSkillId(),
                request.getName(),
                request.getDescription(),
                request.getTags(),
                request.getExamples(),
                request.getInputModes(),
                request.getOutputModes()
        );

        AgentSkill saved = skillRepository.save(skill);
        log.info("Skill 创建成功: id={}, cardId={}", id, cardId);
        return CardAssembler.toSkillDTO(saved);
    }

    @Override
    @Transactional
    public AgentSkillDTO update(Long cardId, AgentSkillUpdateRequest request) {
        AgentSkill skill = skillRepository.findById(request.getId());
        if (skill == null) {
            throw new com.agenthub.common.domain.exception.DomainException("Skill not found: " + request.getId(), "SKILL_NOT_FOUND") {};
        }
        if (!skill.getCardId().equals(cardId)) {
            throw new com.agenthub.common.domain.exception.DomainException(
                    "Skill " + request.getId() + " does not belong to card " + cardId, "SKILL_CARD_MISMATCH") {};
        }

        skill.update(
                request.getName(),
                request.getDescription(),
                request.getTags(),
                request.getExamples(),
                request.getInputModes(),
                request.getOutputModes()
        );

        AgentSkill updated = skillRepository.update(skill);
        log.info("Skill 更新成功: id={}", request.getId());
        return CardAssembler.toSkillDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long cardId, Long id) {
        AgentSkill skill = skillRepository.findById(id);
        if (skill == null) {
            return;
        }
        if (!skill.getCardId().equals(cardId)) {
            throw new com.agenthub.common.domain.exception.DomainException(
                    "Skill " + id + " does not belong to card " + cardId, "SKILL_CARD_MISMATCH") {};
        }
        skillRepository.deleteById(id);
        log.info("Skill 删除成功: id={}", id);
    }

    @Override
    public List<AgentSkillDTO> getByCardId(Long cardId) {
        List<AgentSkill> skills = skillRepository.findByCardId(cardId);
        return skills.stream().map(CardAssembler::toSkillDTO).collect(Collectors.toList());
    }
}
