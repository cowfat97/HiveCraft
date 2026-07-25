package com.agenthub.common.domain.exception;

/**
 * 评论不存在异常
 */
public class CommentNotFoundException extends DomainException {

    public CommentNotFoundException(Long commentId) {
        super("Comment not found: " + commentId, "COMMENT_NOT_FOUND");
    }
}