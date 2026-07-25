package com.agenthub.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 更新请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentUpdateRequest {

    private Long id;

    private String name;

    private String description;

    private String endpoint;

    private String version;

    private String type;

    // A2A 字段
    private String protocolVersion;
    private String preferredTransport;
    private String capabilities;
    private String securitySchemes;
    private String security;
    private String provider;
    private String defaultInputModes;
    private String defaultOutputModes;
    private String iconUrl;
    private String documentationUrl;
    private Boolean supportsAuthenticatedExtendedCard;
}