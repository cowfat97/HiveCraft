package com.agenthub.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardUpdateRequest {

    private Long id;

    private String name;

    private String description;

    private String content;

    private String summary;

    private List<String> tags;
}
