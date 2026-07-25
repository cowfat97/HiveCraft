package com.agenthub.card.controller;

import com.agenthub.card.dto.AgentCardCreateRequest;
import com.agenthub.card.dto.AgentCardListResponse;
import com.agenthub.card.dto.AgentCardQueryRequest;
import com.agenthub.card.dto.AgentCardUpdateRequest;
import com.agenthub.card.service.AgentCardService;
import com.agenthub.common.dto.AgentCardDTO;
import com.agenthub.common.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "AgentCard", description = "Agent Card 管理 API")
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Validated
public class AgentCardController {

    private final AgentCardService cardService;

    @Operation(summary = "创建 Card", description = "为 Agent 创建一个展示卡片")
    @PostMapping
    public ApiResponse<AgentCardDTO> create(@Valid @RequestBody AgentCardCreateRequest request) {
        return ApiResponse.success(cardService.create(request));
    }

    @Operation(summary = "更新 Card")
    @PutMapping("/{id}")
    public ApiResponse<AgentCardDTO> update(
            @Parameter(description = "Card ID") @PathVariable Long id,
            @RequestBody AgentCardUpdateRequest request) {
        request.setId(id);
        return ApiResponse.success(cardService.update(request));
    }

    @Operation(summary = "删除 Card")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        cardService.delete(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "获取 Card 详情")
    @GetMapping("/{id}")
    public ApiResponse<AgentCardDTO> getDetail(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.getDetail(id));
    }

    @Operation(summary = "获取 Card 内容", description = "返回 Markdown 格式的 Card 正文")
    @GetMapping("/{id}/content")
    public ApiResponse<String> getContent(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.getContent(id));
    }

    @Operation(summary = "Card 列表", description = "支持关键词/Agent/状态筛选和分页")
    @GetMapping
    public ApiResponse<AgentCardListResponse> list(AgentCardQueryRequest request) {
        return ApiResponse.success(cardService.list(request));
    }

    @Operation(summary = "搜索 Card", description = "按关键词搜索 Card")
    @GetMapping("/search")
    public ApiResponse<AgentCardListResponse> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(cardService.search(keyword, pageNum, pageSize));
    }

    @Operation(summary = "按 Agent 查询 Card")
    @GetMapping("/agent/{agentId}")
    public ApiResponse<AgentCardListResponse> findByAgent(
            @Parameter(description = "Agent ID") @PathVariable Long agentId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(cardService.findByAgent(agentId, pageNum, pageSize));
    }

    @Operation(summary = "发布 Card", description = "Card 状态从 DRAFT 变为 PUBLISHED")
    @PostMapping("/{id}/publish")
    public ApiResponse<AgentCardDTO> publish(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.publish(id));
    }

    @Operation(summary = "归档 Card")
    @PostMapping("/{id}/archive")
    public ApiResponse<AgentCardDTO> archive(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.archive(id));
    }

    @Operation(summary = "取消归档")
    @PostMapping("/{id}/unarchive")
    public ApiResponse<AgentCardDTO> unarchive(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.unarchive(id));
    }

    @Operation(summary = "点赞 Card")
    @PostMapping("/{id}/like")
    public ApiResponse<AgentCardDTO> like(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ApiResponse.success(cardService.like(id));
    }
}
