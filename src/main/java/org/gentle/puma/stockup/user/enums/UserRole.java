package org.gentle.puma.stockup.user.enums;

public enum UserRole {
    ADMIN, ENTERPRISE;

    public static boolean isValid(String value) {
        try {
            UserRole.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
