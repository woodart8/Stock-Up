package org.gentle.puma.stockup.auth.service;

import org.gentle.puma.stockup.auth.dto.request.LoginRequestDto;
import org.gentle.puma.stockup.auth.dto.request.SignUpRequestDto;
import org.gentle.puma.stockup.auth.dto.response.LoginResponseDto;
import org.gentle.puma.stockup.auth.dto.response.TokenResponseDto;

public interface AuthService {

    Long signUp(SignUpRequestDto req);

    LoginResponseDto login(LoginRequestDto req);

    LoginResponseDto kakaoLogin(String accessCode);

    LoginResponseDto naverLogin(String accessCode);

    void logout(String token);

    TokenResponseDto reissue(String refreshToken);

}
