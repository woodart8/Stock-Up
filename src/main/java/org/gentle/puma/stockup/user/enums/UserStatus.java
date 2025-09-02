package org.gentle.puma.stockup.user.enums;

public enum UserStatus {
    ACTIVE, INACTIVE, DELETED;

    public static boolean isValid(String value) {
        try {
            UserStatus.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
