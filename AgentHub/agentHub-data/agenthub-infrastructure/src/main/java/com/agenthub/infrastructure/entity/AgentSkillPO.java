package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent Skill 持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSkillPO {

    private Long id;
    private Long cardId;
    private String skillId;
    private String name;
    private String description;
    private String tags;
    private String examples;
    private String inputModes;
    private String outputModes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
