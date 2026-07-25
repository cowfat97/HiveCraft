package com.agenthub.card.domain.entity;

import com.agenthub.common.domain.event.DomainEvent;
import com.agenthub.common.domain.event.card.CardPublishedEvent;
import com.agenthub.common.domain.event.card.CardUpdatedEvent;
import com.agenthub.common.domain.exception.CardStateException;
import com.agenthub.common.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent Card 聚合根
 *
 * Agent 的公开展示卡片，介绍 Agent 功能
 * 状态机: DRAFT -> PUBLISHED -> ARCHIVED -> PUBLISHED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCard {

    /**
     * Card 唯一标识（雪花算法生成）
     */
    private Long id;

    /**
     * 关联 Agent ID
     */
    private Long agentId;

    /**
     * Card 名称
     */
    private String name;

    /**
     * Card 描述
     */
    private String description;

    /**
     * Card 内容（Markdown）
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * Card 状态
     */
    private CardStatus status;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 领域事件列表
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ==================== 领域行为 ====================

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * 创建新 Card（工厂方法）
     */
    public static AgentCard create(Long id, Long agentId, String name, String description) {
        return AgentCard.builder()
                .id(id)
                .agentId(agentId)
                .name(name)
                .description(description)
                .status(CardStatus.DRAFT)
                .viewCount(0L)
                .likeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新 Card 信息
     */
    public void update(String name, String description, String summary, List<String> tags) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (summary != null) this.summary = summary;
        if (tags != null) this.tags = tags;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new CardUpdatedEvent(this.id, this.agentId, this.name));
    }

    /**
     * 发布 Card
     */
    public void publish() {
        if (this.status == CardStatus.PUBLISHED) {
            return;
        }
        if (this.status != CardStatus.DRAFT) {
            throw new CardStateException(this.id, this.status.getCode(), "PUBLISHED");
        }
        this.status = CardStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new CardPublishedEvent(this.id, this.agentId, this.name));
    }

    /**
     * 归档 Card
     */
    public void archive() {
        if (this.status != CardStatus.PUBLISHED) {
            throw new CardStateException(this.id, this.status.getCode(), "ARCHIVED");
        }
        this.status = CardStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 取消归档
     */
    public void unarchive() {
        if (this.status != CardStatus.ARCHIVED) {
            throw new CardStateException(this.id, this.status.getCode(), "PUBLISHED");
        }
        this.status = CardStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public void like() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    public boolean isPublished() {
        return this.status == CardStatus.PUBLISHED;
    }

    public boolean isEditable() {
        return this.status == CardStatus.DRAFT;
    }
}
