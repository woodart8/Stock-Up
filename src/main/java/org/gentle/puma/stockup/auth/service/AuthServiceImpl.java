package org.gentle.puma.stockup.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.gentle.puma.stockup.auth.dto.KakaoDto;
import org.gentle.puma.stockup.auth.dto.NaverDto;
import org.gentle.puma.stockup.auth.dto.request.LoginRequestDto;
import org.gentle.puma.stockup.auth.dto.request.SignUpRequestDto;
import org.gentle.puma.stockup.auth.dto.response.LoginResponseDto;
import org.gentle.puma.stockup.auth.dto.response.TokenResponseDto;
import org.gentle.puma.stockup.auth.utility.KakaoUtil;
import org.gentle.puma.stockup.auth.utility.NaverUtil;
import org.gentle.puma.stockup.auth.validator.LoginValidator;
import org.gentle.puma.stockup.auth.validator.SignUpValidator;
import org.gentle.puma.stockup.common.exception.CommonException;
import org.gentle.puma.stockup.common.exception.ErrorCode;
import org.gentle.puma.stockup.common.redis.utility.RedisUtil;
import org.gentle.puma.stockup.common.security.jwt.JwtProperties;
import org.gentle.puma.stockup.common.security.jwt.JwtToken;
import org.gentle.puma.stockup.common.security.jwt.utility.JwtUtil;
import org.gentle.puma.stockup.user.entity.UserEntity;
import org.gentle.puma.stockup.user.enums.SignUpPath;
import org.gentle.puma.stockup.user.enums.UserRole;
import org.gentle.puma.stockup.user.enums.UserStatus;
import org.gentle.puma.stockup.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;
    private final KakaoUtil kakaoUtil;
    private final NaverUtil naverUtil;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JwtProperties jwtProperties,
                           RedisUtil redisUtil,
                           KakaoUtil kakaoUtil,
                           NaverUtil naverUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.redisUtil = redisUtil;
        this.kakaoUtil = kakaoUtil;
        this.naverUtil = naverUtil;
    }

    @Override
    public Long signUp(SignUpRequestDto req) {
        SignUpValidator.validate(req);

        UserEntity user = userRepository.findByEmailAndSignUpPath(req.getEmail(), SignUpPath.valueOf(req.getSignUpPath()))
                .orElse(null);

        if (user != null) throw new CommonException(ErrorCode.DUPLICATE_USER);

        return userRepository.save(UserEntity.builder()
                .nickname(req.getNickname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.valueOf(req.getSignUpPath()))
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE)
                .build()
        ).getUserId();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto req) {
        LoginValidator.validate(req);

        UserEntity user = userRepository.findByEmailAndSignUpPath(req.getEmail(), SignUpPath.valueOf(req.getSignUpPath()))
                .orElse(null);

        if (user == null) {
            throw new CommonException(ErrorCode.INVALID_EMAIL);
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public LoginResponseDto kakaoLogin(String accessCode) {
        KakaoDto.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDto.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String email = kakaoProfile.getKakaoAccount().getEmail();

        UserEntity user = userRepository.findByEmailAndSignUpPath(email, SignUpPath.KAKAO)
                .orElseGet(() -> createNewUser(kakaoProfile));

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public LoginResponseDto naverLogin(String accessCode) {
        NaverDto.OAuthToken oAuthToken = naverUtil.requestToken(accessCode);
        NaverDto.NaverProfile naverProfile = naverUtil.requestProfile(oAuthToken);
        String email = naverProfile.getEmail();

        UserEntity user = userRepository.findByEmailAndSignUpPath(email, SignUpPath.NAVER)
                .orElseGet(() -> createNewUser(naverProfile));

        JwtToken jwtToken = jwtUtil.createAuthTokens(user);
        redisUtil.setValue(
                "refresh:" + user.getUserId(),
                jwtToken.getRefreshToken(),
                jwtProperties.getRefreshExpirationTime(),
                TimeUnit.SECONDS
        );

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .token(jwtToken)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        jwtUtil.validateToken(accessToken);
        String userId = jwtUtil.getSubject(accessToken);
        redisUtil.deleteValue("refresh:" + userId);
    }

    @Override
    public TokenResponseDto reissue(String refreshToken) {
        jwtUtil.validateToken(refreshToken);
        String subject = jwtUtil.getSubject(refreshToken);

        UserEntity user;
        try {
            user = userRepository.findById(Long.parseLong(subject))
                    .orElse(null);
        } catch (NumberFormatException e) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }

        if (user == null) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }

        String storedToken = redisUtil.getValue("refresh:" + user.getUserId());
        if (refreshToken.equals(storedToken)) {
            JwtToken jwtToken = jwtUtil.reissueAccessToken(user, refreshToken);
            return TokenResponseDto.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }
    }

    private UserEntity createNewUser(KakaoDto.KakaoProfile kakaoProfile) {
        UserEntity newUser = UserEntity.builder()
                .nickname(kakaoProfile.getKakaoAccount().getProfile().getNickname())
                .email(kakaoProfile.getKakaoAccount().getEmail())
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.KAKAO)
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE)
                .build();
        return userRepository.save(newUser);
    }

    private UserEntity createNewUser(NaverDto.NaverProfile naverProfile) {
        UserEntity newUser = UserEntity.builder()
                .nickname(naverProfile.getNickname())
                .email(naverProfile.getEmail())
                .userStatus(UserStatus.ACTIVE)
                .signUpPath(SignUpPath.NAVER)
                .createdAt(LocalDateTime.now().withNano(0))
                .userRole(UserRole.ENTERPRISE)
                .build();
        return userRepository.save(newUser);
    }

}
