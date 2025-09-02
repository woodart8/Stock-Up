package org.gentle.puma.stockup.auth.controller;

import org.gentle.puma.stockup.auth.dto.request.LoginRequestDto;
import org.gentle.puma.stockup.auth.dto.request.SignUpRequestDto;
import org.gentle.puma.stockup.auth.dto.response.LoginResponseDto;
import org.gentle.puma.stockup.auth.dto.response.TokenResponseDto;
import org.gentle.puma.stockup.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto req) {
        Long userId = authService.signUp(req);
        return ResponseEntity.ok(userId);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto req) {
        LoginResponseDto loginResponse = authService.login(req);
        return ResponseEntity.ok(loginResponse);
    }

    // 카카오 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode) {
        LoginResponseDto loginResponse = authService.kakaoLogin(accessCode);
        return ResponseEntity.ok(loginResponse);
    }

    // 네이버 로그인
    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLogin(@RequestParam("code") String accessCode) {
        LoginResponseDto loginResponse = authService.naverLogin(accessCode);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 엑세스 토큰 갱신
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponseDto tokenResponse = authService.reissue(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

}
