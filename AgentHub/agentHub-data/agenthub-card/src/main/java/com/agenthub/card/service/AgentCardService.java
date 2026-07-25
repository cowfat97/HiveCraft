package com.agenthub.card.service;

import com.agenthub.card.dto.AgentCardCreateRequest;
import com.agenthub.card.dto.AgentCardListResponse;
import com.agenthub.card.dto.AgentCardQueryRequest;
import com.agenthub.card.dto.AgentCardUpdateRequest;
import com.agenthub.common.dto.AgentCardDTO;

public interface AgentCardService {

    AgentCardDTO create(AgentCardCreateRequest request);

    AgentCardDTO update(AgentCardUpdateRequest request);

    void delete(Long id);

    AgentCardDTO getDetail(Long id);

    AgentCardDTO getById(Long id);

    String getContent(Long id);

    AgentCardListResponse list(AgentCardQueryRequest request);

    AgentCardListResponse search(String keyword, int pageNum, int pageSize);

    AgentCardListResponse findByAgent(Long agentId, int pageNum, int pageSize);

    AgentCardDTO publish(Long id);

    AgentCardDTO archive(Long id);

    AgentCardDTO unarchive(Long id);

    AgentCardDTO like(Long id);
}
