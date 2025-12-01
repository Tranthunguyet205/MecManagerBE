package com.example.mecManager.common.enums;

/**
 * User status enumeration for approval workflow
 */
public enum UserStatusEnum {
    PENDING("Chờ phê duyệt"),
    APPROVED("Đã phê duyệt"),
    REJECTED("Từ chối");

    private final String description;

    UserStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
