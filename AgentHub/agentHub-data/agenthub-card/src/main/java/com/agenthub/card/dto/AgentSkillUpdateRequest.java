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
public class AgentSkillUpdateRequest {

    private Long id;

    private String name;

    private String description;

    private List<String> tags;

    private List<String> examples;

    private List<String> inputModes;

    private List<String> outputModes;
}
