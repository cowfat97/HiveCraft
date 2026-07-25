package com.agenthub.recommendation.service.impl;

import com.agenthub.common.domain.exception.CommentNotFoundException;
import com.agenthub.common.dto.CommentDTO;
import com.agenthub.common.enums.CommentStatus;
import com.agenthub.common.exception.AgentHubException;
import com.agenthub.common.utils.XssUtils;
import com.agenthub.recommendation.domain.entity.Comment;
import com.agenthub.recommendation.domain.repository.CommentRepository;
import com.agenthub.recommendation.dto.CommentCreateRequest;
import com.agenthub.recommendation.dto.CommentListResponse;
import com.agenthub.recommendation.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private CommentDTO toDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDTO.builder()
                .id(comment.getId())
                .cardId(comment.getCardId())
                .commenterId(comment.getCommenterId())
                .commenterName(comment.getCommenterName())
                .commenterType(comment.getCommenterType())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .replyToId(comment.getReplyToId())
                .replyToName(comment.getReplyToName())
                .status(comment.getStatus() != null ? comment.getStatus().getCode() : null)
                .reviewReason(comment.getReviewReason())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private CommentDTO toDTOWithReplies(Comment comment, List<Comment> replies) {
        CommentDTO dto = toDTO(comment);
        if (dto != null && replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @Override
    @Transactional
    public CommentDTO create(CommentCreateRequest request) {
        Long commentId = commentRepository.nextId();
        Comment comment;

        // XSS过滤评论内容
        String sanitizedContent = XssUtils.sanitizeComment(request.getContent());
        String sanitizedCommenterName = XssUtils.escape(request.getCommenterName());

        if (request.getParentId() == null) {
            comment = Comment.createForCard(
                    commentId,
                    request.getCardId(),
                    request.getCommenterId(),
                    sanitizedCommenterName,
                    request.getCommenterType(),
                    sanitizedContent
            );
        } else {
            Comment parentComment = commentRepository.findById(request.getParentId());
            if (parentComment == null) {
                throw new CommentNotFoundException(request.getParentId());
            }

            if (!parentComment.getCardId().equals(request.getCardId())) {
                throw new AgentHubException("父评论不属于该Card");
            }

            Long rootId = parentComment.getRootId();

            // XSS过滤回复目标名称
            String sanitizedReplyToName = request.getReplyToName() != null
                    ? XssUtils.escape(request.getReplyToName()) : null;

            comment = Comment.createReply(
                    commentId,
                    request.getCardId(),
                    request.getCommenterId(),
                    sanitizedCommenterName,
                    request.getCommenterType(),
                    sanitizedContent,
                    request.getParentId(),
                    rootId,
                    request.getReplyToId(),
                    sanitizedReplyToName
            );
        }

        Comment saved = commentRepository.save(comment);
        log.info("创建评论成功: id={}, cardId={}", commentId, request.getCardId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public CommentDTO approve(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        comment.approve();
        Comment updated = commentRepository.update(comment);

        log.info("评论审核通过: id={}", id);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public CommentDTO reject(Long id, String reason) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        comment.reject(reason);
        Comment updated = commentRepository.update(comment);

        log.info("评论审核拒绝: id={}, reason={}", id, reason);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, Long commenterId, String commenterType) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        if (!comment.isCommenter(commenterId, commenterType)) {
            throw new AgentHubException("只有评论者可以删除评论");
        }

        comment.delete();
        commentRepository.update(comment);

        log.info("删除评论成功: id={}, commenterId={}", id, commenterId);
    }

    @Override
    public CommentDTO getById(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }
        return toDTO(comment);
    }

    @Override
    public CommentListResponse getCardComments(Long cardId, int pageNum, int pageSize) {
        List<Comment> topComments = commentRepository.findByCardIdAndStatusWithPage(
                cardId, CommentStatus.APPROVED, pageNum, pageSize);

        long total = commentRepository.countByCardIdAndStatus(cardId, CommentStatus.APPROVED);

        List<CommentDTO> commentDTOs = topComments.stream()
                .map(comment -> {
                    List<Comment> replies = commentRepository.findRepliesByRootId(comment.getId())
                            .stream()
                            .filter(c -> c.getStatus() == CommentStatus.APPROVED)
                            .collect(Collectors.toList());
                    return toDTOWithReplies(comment, replies);
                })
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public CommentListResponse getReplies(Long rootId, int pageNum, int pageSize) {
        List<Comment> replies = commentRepository.findRepliesByRootIdWithPage(rootId, pageNum, pageSize)
                .stream()
                .filter(c -> c.getStatus() == CommentStatus.APPROVED && !c.getId().equals(rootId))
                .collect(Collectors.toList());

        long total = commentRepository.countRepliesByRootId(rootId);

        List<CommentDTO> commentDTOs = replies.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public CommentListResponse getPendingComments(int pageNum, int pageSize) {
        List<Comment> pendingComments = commentRepository.findByStatusWithPage(CommentStatus.PENDING, pageNum, pageSize);

        long total = commentRepository.countByStatus(CommentStatus.PENDING);

        List<CommentDTO> commentDTOs = pendingComments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    @Transactional
    public CommentDTO like(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        if (comment.isApproved()) {
            comment.like();
            commentRepository.update(comment);
        }

        return toDTO(comment);
    }

    @Override
    public long countByCardId(Long cardId) {
        return commentRepository.countByCardIdAndStatus(cardId, CommentStatus.APPROVED);
    }
}