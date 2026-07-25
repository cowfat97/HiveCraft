package com.agenthub.common.enums;

import lombok.Getter;

/**
 * Agent Card 状态枚举
 */
@Getter
public enum CardStatus {

    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布"),
    ARCHIVED("ARCHIVED", "已归档");

    private final String code;
    private final String desc;

    CardStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CardStatus fromCode(String code) {
        if (code == null) return null;
        for (CardStatus status : values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
