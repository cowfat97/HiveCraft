package com.agenthub.agent.domain.entity;

import com.agenthub.common.domain.event.DomainEvent;
import com.agenthub.common.domain.event.agent.AgentActivatedEvent;
import com.agenthub.common.domain.event.agent.AgentDeactivatedEvent;
import com.agenthub.common.domain.event.agent.AgentDeletedEvent;
import com.agenthub.common.domain.event.agent.AgentRegisteredEvent;
import com.agenthub.common.domain.exception.AgentAlreadyDeletedException;
import com.agenthub.common.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent 聚合根
 *
 * 统一管理 Agent 的注册、发现、状态管理等行为
 * 合并了原 registration 和 discovery 两个模块的 Agent 概念
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    /**
     * Agent 唯一标识（雪花算法生成）
     */
    private Long id;

    /**
     * Agent 名称（全局唯一）
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

    // ==================== A2A 协议字段 ====================

    /**
     * A2A 协议版本，如 "0.3.0"
     */
    private String protocolVersion;

    /**
     * 首选传输协议: jsonrpc / grpc
     */
    private String preferredTransport;

    /**
     * 能力声明 JSON: {streaming, pushNotifications, stateTransitionHistory}
     */
    private String capabilities;

    /**
     * 安全方案 JSON: [{type, name, in, description}]
     */
    private String securitySchemes;

    /**
     * 安全要求 JSON
     */
    private String security;

    /**
     * 提供商信息 JSON: {organization, url}
     */
    private String provider;

    /**
     * 默认输入模式 JSON: ["text/plain", ...]
     */
    private String defaultInputModes;

    /**
     * 默认输出模式 JSON: ["text/plain", ...]
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
     * 领域事件列表
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ==================== 领域行为 ====================

    /**
     * 获取并清空领域事件
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * 注册新 Agent（工厂方法）
     */
    public static Agent register(Long id, String name, String description,
                                  String endpoint, String version, String type,
                                  String protocolVersion, String preferredTransport,
                                  String capabilities, String securitySchemes,
                                  String security, String provider,
                                  String defaultInputModes, String defaultOutputModes,
                                  String iconUrl, String documentationUrl,
                                  Boolean supportsAuthenticatedExtendedCard) {
        Agent agent = Agent.builder()
                .id(id)
                .name(name)
                .description(description)
                .endpoint(endpoint)
                .version(version)
                .type(type)
                .status(AgentStatus.ONLINE)
                .protocolVersion(protocolVersion)
                .preferredTransport(preferredTransport)
                .capabilities(capabilities)
                .securitySchemes(securitySchemes)
                .security(security)
                .provider(provider)
                .defaultInputModes(defaultInputModes)
                .defaultOutputModes(defaultOutputModes)
                .iconUrl(iconUrl)
                .documentationUrl(documentationUrl)
                .supportsAuthenticatedExtendedCard(supportsAuthenticatedExtendedCard)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 发布注册事件
        agent.domainEvents.add(new AgentRegisteredEvent(id, name, description, endpoint, version));

        return agent;
    }

    /**
     * 更新 Agent 信息
     */
    public void update(String name, String description, String endpoint, String version, String type,
                        String protocolVersion, String preferredTransport,
                        String capabilities, String securitySchemes,
                        String security, String provider,
                        String defaultInputModes, String defaultOutputModes,
                        String iconUrl, String documentationUrl,
                        Boolean supportsAuthenticatedExtendedCard) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (endpoint != null) this.endpoint = endpoint;
        if (version != null) this.version = version;
        if (type != null) this.type = type;
        if (protocolVersion != null) this.protocolVersion = protocolVersion;
        if (preferredTransport != null) this.preferredTransport = preferredTransport;
        if (capabilities != null) this.capabilities = capabilities;
        if (securitySchemes != null) this.securitySchemes = securitySchemes;
        if (security != null) this.security = security;
        if (provider != null) this.provider = provider;
        if (defaultInputModes != null) this.defaultInputModes = defaultInputModes;
        if (defaultOutputModes != null) this.defaultOutputModes = defaultOutputModes;
        if (iconUrl != null) this.iconUrl = iconUrl;
        if (documentationUrl != null) this.documentationUrl = documentationUrl;
        if (supportsAuthenticatedExtendedCard != null) this.supportsAuthenticatedExtendedCard = supportsAuthenticatedExtendedCard;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活 Agent（上线）
     */
    public void activate() {
        if (this.status == AgentStatus.ONLINE) {
            return;
        }
        this.status = AgentStatus.ONLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentActivatedEvent(this.id));
    }

    /**
     * 停用 Agent（离线）
     */
    public void deactivate() {
        if (this.status != AgentStatus.ONLINE) {
            return;
        }
        this.status = AgentStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentDeactivatedEvent(this.id));
    }

    /**
     * 删除 Agent（软删除）
     */
    public void delete() {
        this.status = AgentStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentDeletedEvent(this.id));
    }

    /**
     * 离线（兼容旧代码）
     */
    public void offline() {
        deactivate();
    }

    /**
     * 上线（兼容旧代码）
     */
    public void online() {
        activate();
    }

    /**
     * 是否活跃
     */
    public boolean isActive() {
        return this.status == AgentStatus.ONLINE;
    }

    /**
     * 是否在线
     */
    public boolean isOnline() {
        return this.status == AgentStatus.ONLINE;
    }
}