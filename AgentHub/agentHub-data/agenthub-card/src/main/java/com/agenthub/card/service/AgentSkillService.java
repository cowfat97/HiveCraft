package com.agenthub.card.service;

import com.agenthub.card.dto.AgentSkillCreateRequest;
import com.agenthub.card.dto.AgentSkillUpdateRequest;
import com.agenthub.common.dto.AgentSkillDTO;

import java.util.List;

public interface AgentSkillService {

    AgentSkillDTO create(Long cardId, AgentSkillCreateRequest request);

    AgentSkillDTO update(Long cardId, AgentSkillUpdateRequest request);

    void delete(Long id);

    List<AgentSkillDTO> getByCardId(Long cardId);
}
