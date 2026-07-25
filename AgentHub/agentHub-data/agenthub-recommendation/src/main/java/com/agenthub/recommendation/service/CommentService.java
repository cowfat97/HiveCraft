package com.agenthub.recommendation.service;

import com.agenthub.common.dto.CommentDTO;
import com.agenthub.recommendation.dto.CommentCreateRequest;
import com.agenthub.recommendation.dto.CommentListResponse;

/**
 * 评论服务接口
 */
public interface CommentService {

    CommentDTO create(CommentCreateRequest request);

    CommentDTO approve(Long id);

    CommentDTO reject(Long id, String reason);

    void delete(Long id, Long commenterId, String commenterType);

    CommentDTO getById(Long id);

    CommentListResponse getCardComments(Long cardId, int pageNum, int pageSize);

    CommentListResponse getReplies(Long rootId, int pageNum, int pageSize);

    CommentListResponse getPendingComments(int pageNum, int pageSize);

    CommentDTO like(Long id);

    long countByCardId(Long cardId);
}