package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 收藏目标类型枚举
 */
@Getter
public enum FavoriteTargetType {

    AGENT_CARD("agent_card", "Agent Card");

    private final String code;
    private final String desc;

    FavoriteTargetType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FavoriteTargetType fromCode(String code) {
        for (FavoriteTargetType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}