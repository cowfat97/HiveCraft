package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent Card 数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long agentId;
    private String name;
    private String description;
    private String content;
    private String summary;
    private List<String> tags;
    private String status;
    private Long viewCount;
    private Long likeCount;
    private List<AgentSkillDTO> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
