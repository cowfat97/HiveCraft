package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent Card 持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardPO {

    private Long id;
    private Long agentId;
    private String name;
    private String description;
    private String content;
    private String summary;
    private String tags;
    private String status;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
