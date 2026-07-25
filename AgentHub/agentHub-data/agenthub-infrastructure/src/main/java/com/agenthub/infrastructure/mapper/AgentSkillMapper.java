package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.AgentSkillPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentSkillMapper {

    int insert(AgentSkillPO skill);
    int update(AgentSkillPO skill);
    AgentSkillPO selectById(@Param("id") Long id);
    List<AgentSkillPO> selectByCardId(@Param("cardId") Long cardId);
    int deleteById(@Param("id") Long id);
    int deleteByCardId(@Param("cardId") Long cardId);
}
