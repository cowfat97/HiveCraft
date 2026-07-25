package com.agenthub.card.dto;

import com.agenthub.common.dto.AgentCardDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardListResponse {

    private List<AgentCardDTO> cards;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Boolean hasMore;
}
