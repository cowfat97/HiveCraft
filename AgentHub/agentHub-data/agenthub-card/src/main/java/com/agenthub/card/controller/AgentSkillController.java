package com.agenthub.card.controller;

import com.agenthub.card.dto.AgentSkillCreateRequest;
import com.agenthub.card.dto.AgentSkillUpdateRequest;
import com.agenthub.card.service.AgentSkillService;
import com.agenthub.common.dto.AgentSkillDTO;
import com.agenthub.common.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "AgentSkill", description = "Agent Skill 管理 API")
@RestController
@RequestMapping("/api/v1/cards/{cardId}/skills")
@RequiredArgsConstructor
@Validated
public class AgentSkillController {

    private final AgentSkillService skillService;

    @Operation(summary = "创建 Skill", description = "为 Card 添加一个技能")
    @PostMapping
    public ApiResponse<AgentSkillDTO> create(
            @Parameter(description = "Card ID") @PathVariable Long cardId,
            @Valid @RequestBody AgentSkillCreateRequest request) {
        return ApiResponse.success(skillService.create(cardId, request));
    }

    @Operation(summary = "更新 Skill")
    @PutMapping("/{id}")
    public ApiResponse<AgentSkillDTO> update(
            @Parameter(description = "Card ID") @PathVariable Long cardId,
            @Parameter(description = "Skill ID") @PathVariable Long id,
            @RequestBody AgentSkillUpdateRequest request) {
        request.setId(id);
        return ApiResponse.success(skillService.update(cardId, request));
    }

    @Operation(summary = "删除 Skill")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "Card ID") @PathVariable Long cardId,
            @Parameter(description = "Skill ID") @PathVariable Long id) {
        skillService.delete(cardId, id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "查询 Card 下所有 Skill")
    @GetMapping
    public ApiResponse<List<AgentSkillDTO>> getByCardId(
            @Parameter(description = "Card ID") @PathVariable Long cardId) {
        return ApiResponse.success(skillService.getByCardId(cardId));
    }
}
