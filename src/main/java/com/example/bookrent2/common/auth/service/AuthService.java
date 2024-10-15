package com.example.bookrent2.common.auth.service;


import com.example.bookrent2.common.auth.jwt.JwtProvider;
import com.example.bookrent2.common.auth.refreshToken.RefreshToken;
import com.example.bookrent2.common.auth.refreshToken.RefreshTokenRepository;
import com.example.bookrent2.common.exception.ErrorCode;
import com.example.bookrent2.common.exception.SimpleApplicationException;
import com.example.bookrent2.common.request.AddUserRequest;
import com.example.bookrent2.common.request.LogOutRequest;
import com.example.bookrent2.common.request.LoginRequest;
import com.example.bookrent2.rentBook.model.user.Role;
import com.example.bookrent2.rentBook.model.user.User;
import com.example.bookrent2.rentBook.model.user.UserRepository;
import com.example.bookrent2.rentBook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    @Lazy
    private final UserService userService;

    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    @Lazy
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    // 이메일 가입 
    @Transactional
    public void signUp(AddUserRequest addUserRequest) {
        userRepository.findByEmail(addUserRequest.getEmail()).ifPresent(it -> {
            throw new SimpleApplicationException(ErrorCode.EMAIL_ALREADY_EXISTED);
        });

        if (!addUserRequest.getPassword().equals(addUserRequest.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 비밀번호 확인과 다릅니다.");
        }

        // 기본 Role 직접 생성 ("USER" 역할 부여)
        Role roleUser = new Role("USER");

        userRepository.save(User.builder()
                .email(addUserRequest.getEmail())
                .password(passwordEncoder.encode(addUserRequest.getPassword()))
                .roles(Set.of(roleUser))  // User 역할을 Set으로 설정
                .build());
    }


    // 이메일 가입 사용자 로그아웃
    @Transactional  // 트랜잭션을 적용하여 삭제 작업 처리
    public void logout(LogOutRequest logoutRequest) {
        User user = userService.findByEmail(logoutRequest.getEmail())
                .orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // 해당 사용자의 리프레시 토큰 삭제 (트랜잭션 내에서 처리)
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    // OAuth 사용자 로그아웃
    @Transactional
    public void oauthLogout(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        System.out.println("Extracted token: " + token);
        // JWT 토큰이 유효한지 검증
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new SimpleApplicationException(ErrorCode.JWT_INVALID_TOKEN);
        }

        String email = jwtProvider.getUsernameFromToken(token);
        System.out.println("Extracted email from token: " + email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        System.out.println("Found user: " + user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();  // 인증 정보 제거
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 JWT 토큰 부분만 추출
        }
        return null;
    }


    // 이메일 로그인
    @Transactional
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION)
        );

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        // 인증 처리
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()));

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtProvider.generateAccessTokenForUser(authentication);
        String refreshToken = jwtProvider.generateRefreshTokenForUser(user);

        // Refresh Token을 DB에 저장
        saveRefreshToken(user, refreshToken);

        return accessToken;
    }

    @Transactional
    protected void saveRefreshToken(User user, String refreshToken) {
        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        // 새로운 리프레시 토큰 저장
        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setRefreshToken(refreshToken);
        token.setExpiryDate(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)).toInstant());
        refreshTokenRepository.save(token);
    }


    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

}
