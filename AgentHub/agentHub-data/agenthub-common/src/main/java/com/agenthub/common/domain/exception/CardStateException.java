package com.agenthub.common.domain.exception;

/**
 * Card 状态异常
 */
public class CardStateException extends DomainException {

    public CardStateException(Long cardId, String currentState, String targetState) {
        super("Card " + cardId + " cannot transition from " + currentState + " to " + targetState,
                "CARD_STATE_ERROR");
    }
}
