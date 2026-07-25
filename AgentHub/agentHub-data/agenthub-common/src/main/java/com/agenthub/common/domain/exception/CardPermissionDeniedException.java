package com.agenthub.common.domain.exception;

/**
 * Card 权限异常
 */
public class CardPermissionDeniedException extends DomainException {

    public CardPermissionDeniedException(String message) {
        super(message, "CARD_PERMISSION_DENIED");
    }
}
