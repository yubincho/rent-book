package com.example.bookrent2.common.auth.jwt;

import com.example.bookrent2.common.auth.refreshToken.RefreshToken;
import com.example.bookrent2.common.auth.refreshToken.RefreshTokenRepository;
import com.example.bookrent2.common.exception.ErrorCode;
import com.example.bookrent2.common.exception.SimpleApplicationException;
import com.example.bookrent2.rentBook.model.user.User;


import com.example.bookrent2.rentBook.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    private UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    // UserService에 Setter 주입 적용
    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    // 토큰 유효기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);  // Access Token: 1일
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);  // Refresh Token: 7일

    // Access Token 생성
    public String generateAccessTokenForUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            // OAuth2User로 변환 시도 (구글 로그인 사용자)
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = (String) oAuth2User.getAttributes().get("email");

            User userPrincipal = userService.findByEmail(email)
                    .orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

            return generateToken(userPrincipal, ACCESS_TOKEN_DURATION);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            // 일반 이메일 사용자
//            User userPrincipal = (User) authentication.getPrincipal();
            // 일반 사용자 처리
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();  // UserDetails에서 이메일 가져옴

            // 이메일로 User 엔티티 조회
            User userPrincipal = userService.findByEmail(email)
                    .orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

            return generateToken(userPrincipal, ACCESS_TOKEN_DURATION);
        }

        throw new IllegalArgumentException("Unsupported authentication principal type");
    }

    // Refresh Token 생성
    public String generateRefreshTokenForUser(User user) {
        return generateToken(user, REFRESH_TOKEN_DURATION);
    }

    // 공통 토큰 생성 메서드
    private String generateToken(User user, Duration expirationTime) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime.toMillis()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    // 리프레시 토큰으로 Access Token 갱신
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        // 만료 여부 확인
        if (token.getExpiryDate().isBefore(new Date().toInstant())) {
            refreshTokenRepository.delete(token);  // 만료된 토큰 삭제
            throw new JwtException("Refresh token expired. Please login again.");
        }

        // Refresh Token의 사용자 정보로 새로운 Access Token 생성
        Long userId = token.getUserId();
        User user = userService.getUserById(userId);

        // 새로운 Access Token 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities());

        return generateAccessTokenForUser(authentication);
    }

    // JWT에서 유저네임 추출 -> email 추출로 설정함
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Secret 키 생성
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}

