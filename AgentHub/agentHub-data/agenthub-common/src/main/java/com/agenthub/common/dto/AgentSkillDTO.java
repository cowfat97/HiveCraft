package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent Skill 数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSkillDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long cardId;
    private String skillId;
    private String name;
    private String description;
    private List<String> tags;
    private List<String> examples;
    private List<String> inputModes;
    private List<String> outputModes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
