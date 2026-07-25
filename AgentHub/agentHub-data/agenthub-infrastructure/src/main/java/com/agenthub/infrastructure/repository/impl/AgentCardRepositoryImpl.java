package com.agenthub.infrastructure.repository.impl;

import com.agenthub.card.domain.entity.AgentCard;
import com.agenthub.card.domain.repository.AgentCardRepository;
import com.agenthub.common.enums.CardStatus;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.AgentCardPO;
import com.agenthub.infrastructure.mapper.AgentCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AgentCardRepositoryImpl implements AgentCardRepository {

    private final AgentCardMapper cardMapper;
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== PO 转换 ====================

    private AgentCard toEntity(AgentCardPO po) {
        if (po == null) return null;
        return AgentCard.builder()
                .id(po.getId())
                .agentId(po.getAgentId())
                .name(po.getName())
                .description(po.getDescription())
                .content(po.getContent())
                .summary(po.getSummary())
                .tags(po.getTags() != null ? Arrays.asList(po.getTags().split(",")) : null)
                .status(CardStatus.valueOf(po.getStatus()))
                .viewCount(po.getViewCount())
                .likeCount(po.getLikeCount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .publishedAt(po.getPublishedAt())
                .build();
    }

    private AgentCardPO toPO(AgentCard card) {
        if (card == null) return null;
        return AgentCardPO.builder()
                .id(card.getId())
                .agentId(card.getAgentId())
                .name(card.getName())
                .description(card.getDescription())
                .content(card.getContent())
                .summary(card.getSummary())
                .tags(card.getTags() != null ? String.join(",", card.getTags()) : null)
                .status(card.getStatus() != null ? card.getStatus().name() : CardStatus.DRAFT.name())
                .viewCount(card.getViewCount())
                .likeCount(card.getLikeCount())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .publishedAt(card.getPublishedAt())
                .build();
    }

    // ==================== 写操作 ====================

    @Override
    public AgentCard save(AgentCard card) {
        AgentCardPO po = toPO(card);
        if (po.getCreatedAt() == null) po.setCreatedAt(LocalDateTime.now());
        if (po.getUpdatedAt() == null) po.setUpdatedAt(LocalDateTime.now());
        cardMapper.insert(po);
        return toEntity(po);
    }

    @Override
    public AgentCard update(AgentCard card) {
        AgentCardPO po = toPO(card);
        po.setUpdatedAt(LocalDateTime.now());
        cardMapper.update(po);
        return toEntity(po);
    }

    @Override
    public void deleteById(Long id) {
        cardMapper.deleteById(id);
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }

    // ==================== 读操作 ====================

    @Override
    public AgentCard findById(Long id) {
        return toEntity(cardMapper.selectById(id));
    }

    @Override
    public AgentCard findByAgentId(Long agentId) {
        return toEntity(cardMapper.selectByAgentId(agentId));
    }

    @Override
    public List<AgentCard> findByStatus(CardStatus status) {
        return cardMapper.selectByStatus(status.name()).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AgentCard> findAll() {
        return cardMapper.selectAll().stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AgentCard> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return cardMapper.selectByPage(offset, pageSize).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AgentCard> search(String keyword) {
        return cardMapper.search(keyword).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AgentCard> searchWithPage(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return cardMapper.searchWithPage(keyword, offset, pageSize).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AgentCard> findByStatusAndPage(CardStatus status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return cardMapper.selectByStatusAndPage(status.name(), offset, pageSize).stream()
                .map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return cardMapper.count();
    }

    @Override
    public long countByStatus(CardStatus status) {
        return cardMapper.countByStatus(status.name());
    }

    @Override
    public long countBySearch(String keyword) {
        return cardMapper.countBySearch(keyword);
    }

    @Override
    public boolean existsById(Long id) {
        return cardMapper.selectById(id) != null;
    }
}
