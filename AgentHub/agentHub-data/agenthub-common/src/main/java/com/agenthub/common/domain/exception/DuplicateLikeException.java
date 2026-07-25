package com.agenthub.common.domain.exception;

/**
 * 重复点赞异常
 */
public class DuplicateLikeException extends DomainException {

    public DuplicateLikeException(Long userId, Long targetId, String targetType) {
        super(String.format("User %d already liked %s %d", userId, targetType, targetId), "DUPLICATE_LIKE");
    }
}