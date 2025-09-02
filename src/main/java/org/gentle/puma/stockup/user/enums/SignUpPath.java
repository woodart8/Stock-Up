package org.gentle.puma.stockup.user.enums;

public enum SignUpPath {
    KAKAO, NAVER, GOOGLE, DEFAULT;

    public static boolean isValid(String value) {
        try {
            SignUpPath.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
