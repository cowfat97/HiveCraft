package com.agenthub.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardQueryRequest {

    private String keyword;
    private String status;
    private Long agentId;
    private int pageNum = 1;
    private int pageSize = 10;
}
