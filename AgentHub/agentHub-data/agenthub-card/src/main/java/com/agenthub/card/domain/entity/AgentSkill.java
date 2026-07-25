package com.agenthub.card.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent Skill 实体
 *
 * 一个 AgentCard 下可挂多个 Skill
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSkill {

    /**
     * Skill 唯一标识（雪花算法生成）
     */
    private Long id;

    /**
     * 关联 Card ID
     */
    private Long cardId;

    /**
     * 唯一技能标识
     */
    private String skillId;

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能描述
     */
    private String description;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 示例输入列表
     */
    private List<String> examples;

    /**
     * 输入模式
     */
    private List<String> inputModes;

    /**
     * 输出模式
     */
    private List<String> outputModes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== 领域行为 ====================

    /**
     * 创建新 Skill（工厂方法）
     */
    public static AgentSkill create(Long id, Long cardId, String skillId,
                                     String name, String description,
                                     List<String> tags, List<String> examples,
                                     List<String> inputModes, List<String> outputModes) {
        return AgentSkill.builder()
                .id(id)
                .cardId(cardId)
                .skillId(skillId)
                .name(name)
                .description(description)
                .tags(tags)
                .examples(examples)
                .inputModes(inputModes)
                .outputModes(outputModes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新 Skill 信息
     */
    public void update(String name, String description, List<String> tags,
                        List<String> examples, List<String> inputModes,
                        List<String> outputModes) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (tags != null) this.tags = tags;
        if (examples != null) this.examples = examples;
        if (inputModes != null) this.inputModes = inputModes;
        if (outputModes != null) this.outputModes = outputModes;
        this.updatedAt = LocalDateTime.now();
    }
}
