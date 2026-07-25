package com.agenthub.card.service.impl;

import com.agenthub.card.domain.entity.AgentCard;
import com.agenthub.card.domain.entity.AgentSkill;
import com.agenthub.card.domain.repository.AgentCardRepository;
import com.agenthub.card.domain.repository.AgentSkillRepository;
import com.agenthub.card.dto.AgentCardCreateRequest;
import com.agenthub.card.dto.AgentCardListResponse;
import com.agenthub.card.dto.AgentCardQueryRequest;
import com.agenthub.card.dto.AgentCardUpdateRequest;
import com.agenthub.card.service.AgentCardService;
import com.agenthub.common.domain.event.DomainEventPublisher;
import com.agenthub.common.utils.XssUtils;
import com.agenthub.common.domain.exception.CardNotFoundException;
import com.agenthub.common.domain.exception.CardStateException;
import com.agenthub.common.dto.AgentCardDTO;
import com.agenthub.common.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentCardServiceImpl implements AgentCardService {

    private final AgentCardRepository cardRepository;
    private final AgentSkillRepository skillRepository;
    private final DomainEventPublisher eventPublisher;

    // ==================== CRUD ====================

    @Override
    @Transactional
    public AgentCardDTO create(AgentCardCreateRequest request) {
        Long id = cardRepository.nextId();

        AgentCard card = AgentCard.create(
                id,
                request.getAgentId(),
                request.getName(),
                request.getDescription()
        );
        card.setName(XssUtils.sanitizeTitle(card.getName()));
        card.setDescription(XssUtils.escape(card.getDescription()));
        if (request.getTags() != null) {
            request.getTags().replaceAll(XssUtils::sanitizeTag);
        }
        card.setTags(request.getTags());
        card.setContent(XssUtils.sanitizeCard(request.getContent()));

        AgentCard saved = cardRepository.save(card);

        log.info("Card 创建成功: id={}, agentId={}", id, request.getAgentId());
        return CardAssembler.toCardDTO(saved);
    }

    @Override
    @Transactional
    public AgentCardDTO update(AgentCardUpdateRequest request) {
        AgentCard card = cardRepository.findById(request.getId());
        if (card == null) {
            throw new CardNotFoundException(request.getId());
        }

        card.update(
                request.getName() != null ? XssUtils.sanitizeTitle(request.getName()) : null,
                request.getDescription() != null ? XssUtils.escape(request.getDescription()) : null,
                request.getSummary() != null ? XssUtils.escape(request.getSummary()) : null,
                request.getTags()
        );

        AgentCard updated = cardRepository.update(card);
        eventPublisher.publishAll(card.pullDomainEvents());
        log.info("Card 更新成功: id={}", request.getId());
        return CardAssembler.toCardDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
        log.info("Card 删除成功: id={}", id);
    }

    @Override
    public AgentCardDTO getDetail(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        if (card.isPublished()) {
            card.incrementViewCount();
            cardRepository.update(card);
        }
        AgentCardDTO dto = CardAssembler.toCardDTO(card);
        List<AgentSkill> skills = skillRepository.findByCardId(id);
        dto.setSkills(CardAssembler.toSkillDTOList(skills));
        return dto;
    }

    @Override
    public AgentCardDTO getById(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        return CardAssembler.toCardDTO(card);
    }

    @Override
    public String getContent(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        return card.getContent();
    }

    // ==================== 查询 ====================

    @Override
    public AgentCardListResponse list(AgentCardQueryRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        List<AgentCard> cards;
        long total;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            cards = cardRepository.searchWithPage(request.getKeyword(), pageNum, pageSize);
            total = cardRepository.countBySearch(request.getKeyword());
        } else if (request.getAgentId() != null) {
            AgentCard card = cardRepository.findByAgentId(request.getAgentId());
            cards = card != null ? Collections.singletonList(card) : Collections.emptyList();
            total = cards.size();
        } else if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            CardStatus status = CardStatus.fromCode(request.getStatus());
            cards = cardRepository.findByStatusAndPage(status, pageNum, pageSize);
            total = cardRepository.countByStatus(status);
        } else {
            cards = cardRepository.findByPage(pageNum, pageSize);
            total = cardRepository.count();
        }

        return buildListResponse(cards, total, pageNum, pageSize);
    }

    @Override
    public AgentCardListResponse search(String keyword, int pageNum, int pageSize) {
        List<AgentCard> cards = cardRepository.searchWithPage(keyword, pageNum, pageSize);
        long total = cardRepository.countBySearch(keyword);
        return buildListResponse(cards, total, pageNum, pageSize);
    }

    @Override
    public AgentCardListResponse findByAgent(Long agentId, int pageNum, int pageSize) {
        AgentCard card = cardRepository.findByAgentId(agentId);
        List<AgentCard> cards = card != null ? Collections.singletonList(card) : Collections.emptyList();
        return buildListResponse(cards, (long) cards.size(), pageNum, pageSize);
    }

    // ==================== 状态变更 ====================

    @Override
    @Transactional
    public AgentCardDTO publish(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        card.publish();
        eventPublisher.publishAll(card.pullDomainEvents());
        AgentCard updated = cardRepository.update(card);
        log.info("Card 发布成功: id={}", id);
        return CardAssembler.toCardDTO(updated);
    }

    @Override
    @Transactional
    public AgentCardDTO archive(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        card.archive();
        AgentCard updated = cardRepository.update(card);
        log.info("Card 归档成功: id={}", id);
        return CardAssembler.toCardDTO(updated);
    }

    @Override
    @Transactional
    public AgentCardDTO unarchive(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        card.unarchive();
        AgentCard updated = cardRepository.update(card);
        log.info("Card 取消归档成功: id={}", id);
        return CardAssembler.toCardDTO(updated);
    }

    @Override
    @Transactional
    public AgentCardDTO like(Long id) {
        AgentCard card = cardRepository.findById(id);
        if (card == null) {
            throw new CardNotFoundException(id);
        }
        card.like();
        AgentCard updated = cardRepository.update(card);
        return CardAssembler.toCardDTO(updated);
    }

    // ==================== 私有方法 ====================

    private AgentCardListResponse buildListResponse(List<AgentCard> cards, long total, int pageNum, int pageSize) {
        return AgentCardListResponse.builder()
                .cards(cards.stream().map(CardAssembler::toCardDTO).collect(Collectors.toList()))
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }
}
