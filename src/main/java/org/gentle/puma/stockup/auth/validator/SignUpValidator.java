package org.gentle.puma.stockup.auth.validator;

import org.gentle.puma.stockup.auth.dto.request.SignUpRequestDto;
import org.gentle.puma.stockup.common.exception.CommonException;
import org.gentle.puma.stockup.common.exception.ErrorCode;
import org.gentle.puma.stockup.user.enums.SignUpPath;

public class SignUpValidator {

    public static void validate(SignUpRequestDto req) {
        String nickname = req.getNickname();
        if (nickname == null || nickname.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_NAME);
        }

        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }

        String password = req.getPassword();
        if (password == null || password.length() < 8) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        String signUpPath = req.getSignUpPath();
        if (!SignUpPath.isValid(signUpPath)) {
            throw new CommonException(ErrorCode.INVALID_SIGNUP_PATH);
        }
    }

}
