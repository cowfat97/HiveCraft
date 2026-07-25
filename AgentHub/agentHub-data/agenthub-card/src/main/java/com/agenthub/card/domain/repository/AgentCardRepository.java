package com.agenthub.card.domain.repository;

import com.agenthub.card.domain.entity.AgentCard;
import com.agenthub.common.enums.CardStatus;

import java.util.List;

public interface AgentCardRepository {

    AgentCard save(AgentCard card);
    AgentCard update(AgentCard card);
    AgentCard findById(Long id);
    AgentCard findByAgentId(Long agentId);
    List<AgentCard> findByStatus(CardStatus status);
    List<AgentCard> findAll();
    List<AgentCard> findByPage(int pageNum, int pageSize);
    List<AgentCard> findByStatusAndPage(CardStatus status, int pageNum, int pageSize);
    List<AgentCard> search(String keyword);
    List<AgentCard> searchWithPage(String keyword, int pageNum, int pageSize);
    long count();
    long countByStatus(CardStatus status);
    long countBySearch(String keyword);
    void deleteById(Long id);
    boolean existsById(Long id);
    Long nextId();
}
