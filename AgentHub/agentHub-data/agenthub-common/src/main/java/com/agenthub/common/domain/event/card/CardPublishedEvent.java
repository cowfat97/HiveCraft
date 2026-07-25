package com.agenthub.common.domain.event.card;

import com.agenthub.common.domain.event.DomainEvent;
import lombok.Getter;

/**
 * Card 发布事件
 */
@Getter
public class CardPublishedEvent extends DomainEvent {

    private final Long cardId;
    private final Long agentId;
    private final String name;

    public CardPublishedEvent(Long cardId, Long agentId, String name) {
        super();
        this.cardId = cardId;
        this.agentId = agentId;
        this.name = name;
    }
}
