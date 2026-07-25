package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String endpoint;
    private String version;
    private String type;
    private String status;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
