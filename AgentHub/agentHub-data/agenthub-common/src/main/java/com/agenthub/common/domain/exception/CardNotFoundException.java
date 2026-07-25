package com.agenthub.common.domain.exception;

/**
 * Card 不存在异常
 */
public class CardNotFoundException extends DomainException {

    public CardNotFoundException(Long cardId) {
        super("Card not found: " + cardId, "CARD_NOT_FOUND");
    }

    public CardNotFoundException(String message) {
        super(message, "CARD_NOT_FOUND");
    }
}
