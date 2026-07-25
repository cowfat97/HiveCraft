package com.agenthub.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCardCreateRequest {

    @NotNull(message = "Agent ID 不能为空")
    private Long agentId;

    private String name;

    private String description;

    @NotBlank(message = "Card 内容不能为空")
    private String content;

    private List<String> tags;
}
