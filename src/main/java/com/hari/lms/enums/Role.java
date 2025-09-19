package com.hari.lms.enums;

/**
 * Enum for user roles in the LMS system.
 * 
 * @author Hari Parthu
 */
public enum Role {
    ADMIN("ROLE_ADMIN"),
    INSTRUCTOR("ROLE_INSTRUCTOR"),
    STUDENT("ROLE_STUDENT");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return authority;
    }
}