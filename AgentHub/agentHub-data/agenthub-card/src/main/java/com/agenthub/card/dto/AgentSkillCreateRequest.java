package com.agenthub.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSkillCreateRequest {

    private String skillId;

    @NotBlank(message = "技能名称不能为空")
    private String name;

    private String description;

    private List<String> tags;

    private List<String> examples;

    private List<String> inputModes;

    private List<String> outputModes;
}
