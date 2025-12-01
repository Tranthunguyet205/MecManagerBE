package com.example.mecManager.common.enums;

/**
 * Role enumeration for user access control
 * Defines all available roles in the system
 */
public enum RoleEnum {
    ADMIN("Administrator - Full system access"),
    DOCTOR("Doctor - Can manage prescriptions and patients");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
