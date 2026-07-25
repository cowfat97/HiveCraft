package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.CommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
@Mapper
public interface CommentMapper {

    int insert(CommentPO comment);

    int update(CommentPO comment);

    CommentPO selectById(@Param("id") Long id);

    List<CommentPO> selectByCardId(@Param("cardId") Long cardId);

    List<CommentPO> selectByCardIdAndStatus(@Param("cardId") Long cardId,
                                                @Param("status") String status);

    List<CommentPO> selectRepliesByRootId(@Param("rootId") Long rootId);

    List<CommentPO> selectRepliesByRootIdWithPage(@Param("rootId") Long rootId,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);

    List<CommentPO> selectByStatus(@Param("status") String status);

    List<CommentPO> selectByStatusWithPage(@Param("status") String status,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    long countByStatus(@Param("status") String status);

    List<CommentPO> selectByCommenter(@Param("commenterId") Long commenterId,
                                       @Param("commenterType") String commenterType);

    List<CommentPO> selectByCardIdAndStatusWithPage(@Param("cardId") Long cardId,
                                                        @Param("status") String status,
                                                        @Param("offset") int offset,
                                                        @Param("limit") int limit);

    long countByCardIdAndStatus(@Param("cardId") Long cardId,
                                    @Param("status") String status);

    long countRepliesByRootId(@Param("rootId") Long rootId);

    int deleteById(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);
}