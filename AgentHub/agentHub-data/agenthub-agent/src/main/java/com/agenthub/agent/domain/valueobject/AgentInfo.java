package com.agenthub.agent.domain.valueobject;

import com.agenthub.agent.domain.entity.Agent;
import com.agenthub.common.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 查询视图对象
 *
 * 用于发现模块的查询场景，不包含敏感信息
 * 从 Agent 实体转换而来
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo {

    /**
     * Agent 唯一标识
     */
    private Long id;

    /**
     * Agent 名称
     */
    private String name;

    /**
     * Agent 描述
     */
    private String description;

    /**
     * Agent 服务端点
     */
    private String endpoint;

    /**
     * Agent 版本
     */
    private String version;

    /**
     * Agent 类型
     */
    private String type;

    /**
     * Agent 状态
     */
    private AgentStatus status;

    /**
     * A2A 协议版本
     */
    private String protocolVersion;

    /**
     * 首选传输协议
     */
    private String preferredTransport;

    /**
     * 能力声明 JSON
     */
    private String capabilities;

    /**
     * 安全方案 JSON
     */
    private String securitySchemes;

    /**
     * 安全要求 JSON
     */
    private String security;

    /**
     * 提供商信息 JSON
     */
    private String provider;

    /**
     * 默认输入模式 JSON
     */
    private String defaultInputModes;

    /**
     * 默认输出模式 JSON
     */
    private String defaultOutputModes;

    /**
     * 图标 URL
     */
    private String iconUrl;

    /**
     * 文档 URL
     */
    private String documentationUrl;

    /**
     * 是否支持认证扩展卡片
     */
    private Boolean supportsAuthenticatedExtendedCard;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Agent 实体转换
     */
    public static AgentInfo from(Agent agent) {
        if (agent == null) {
            return null;
        }
        return AgentInfo.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .endpoint(agent.getEndpoint())
                .version(agent.getVersion())
                .type(agent.getType())
                .status(agent.getStatus())
                .protocolVersion(agent.getProtocolVersion())
                .preferredTransport(agent.getPreferredTransport())
                .capabilities(agent.getCapabilities())
                .securitySchemes(agent.getSecuritySchemes())
                .security(agent.getSecurity())
                .provider(agent.getProvider())
                .defaultInputModes(agent.getDefaultInputModes())
                .defaultOutputModes(agent.getDefaultOutputModes())
                .iconUrl(agent.getIconUrl())
                .documentationUrl(agent.getDocumentationUrl())
                .supportsAuthenticatedExtendedCard(agent.getSupportsAuthenticatedExtendedCard())
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }
}