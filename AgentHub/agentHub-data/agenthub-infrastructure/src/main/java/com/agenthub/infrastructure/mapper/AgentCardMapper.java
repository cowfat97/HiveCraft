package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.AgentCardPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentCardMapper {

    int insert(AgentCardPO card);
    int update(AgentCardPO card);
    AgentCardPO selectById(@Param("id") Long id);
    AgentCardPO selectByAgentId(@Param("agentId") Long agentId);
    List<AgentCardPO> selectByStatus(@Param("status") String status);
    List<AgentCardPO> selectAll();
    List<AgentCardPO> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    List<AgentCardPO> search(@Param("keyword") String keyword);
    List<AgentCardPO> searchWithPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    List<AgentCardPO> selectByStatusAndPage(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    long count();
    long countByStatus(@Param("status") String status);
    long countBySearch(@Param("keyword") String keyword);
    int deleteById(@Param("id") Long id);
    int incrementViewCount(@Param("id") Long id);
    int incrementLikeCount(@Param("id") Long id);
}
