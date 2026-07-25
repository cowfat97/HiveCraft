package com.agenthub.recommendation.domain.repository;

import com.agenthub.common.enums.CommentStatus;
import com.agenthub.recommendation.domain.entity.Comment;

import java.util.List;

/**
 * 评论仓储接口
 */
public interface CommentRepository {

    Comment save(Comment comment);

    Comment update(Comment comment);

    Comment findById(Long id);

    List<Comment> findByCardId(Long cardId);

    List<Comment> findByCardIdAndStatus(Long cardId, CommentStatus status);

    List<Comment> findRepliesByRootId(Long rootId);

    List<Comment> findRepliesByRootIdWithPage(Long rootId, int pageNum, int pageSize);

    List<Comment> findByStatus(CommentStatus status);

    List<Comment> findByStatusWithPage(CommentStatus status, int pageNum, int pageSize);

    long countByStatus(CommentStatus status);

    List<Comment> findByCommenter(Long commenterId, String commenterType);

    List<Comment> findByCardIdAndStatusWithPage(Long cardId, CommentStatus status,
                                                    int pageNum, int pageSize);

    long countByCardIdAndStatus(Long cardId, CommentStatus status);

    long countRepliesByRootId(Long rootId);

    void deleteById(Long id);

    boolean existsById(Long id);

    Long nextId();
}