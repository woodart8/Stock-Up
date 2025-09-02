package org.gentle.puma.stockup.auth.validator;

import org.gentle.puma.stockup.auth.dto.request.LoginRequestDto;
import org.gentle.puma.stockup.common.exception.CommonException;
import org.gentle.puma.stockup.common.exception.ErrorCode;
import org.gentle.puma.stockup.user.enums.SignUpPath;

public class LoginValidator {

    public static void validate(LoginRequestDto req) {
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }

        String password = req.getPassword();
        if (password == null || password.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        String signUpPath = req.getSignUpPath();
        if (!SignUpPath.isValid(signUpPath)) {
            throw new CommonException(ErrorCode.INVALID_SIGNUP_PATH);
        }
    }

}
