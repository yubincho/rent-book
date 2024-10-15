package com.example.bookrent2.rentBook.controller;


import com.example.bookrent2.common.auth.jwt.JwtProvider;
import com.example.bookrent2.common.auth.refreshToken.RefreshTokenRequest;
import com.example.bookrent2.common.auth.service.AuthService;
import com.example.bookrent2.common.exception.ErrorCode;
import com.example.bookrent2.common.exception.SimpleApplicationException;
import com.example.bookrent2.common.request.AddUserRequest;
import com.example.bookrent2.common.request.LogOutRequest;
import com.example.bookrent2.common.request.LoginRequest;
import com.example.bookrent2.common.response.JwtResponse;
import com.example.bookrent2.common.response.Response;
import com.example.bookrent2.rentBook.model.user.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;


    // OAuth 사용자 로그아웃
    @PostMapping("/oauth-logout")
    public ResponseEntity<?> oauthLogout(HttpServletRequest request) {
        authService.oauthLogout(request);
        return ResponseEntity.ok(Response.success("Logged out successfully"));
    }


//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            Authentication authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(
//                            request.getEmail(), request.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String jwt = jwtProvider.generateAccessTokenForUser(authentication);
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            User user = (User) userDetails;
//            JwtResponse jwtResponse = new JwtResponse(user.getId(), jwt);
//            return ResponseEntity.ok(new ApiResponse("Login Successful", jwtResponse));
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
//        }
//
//    }

    // 이메일 가입한 사용자 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String jwt = authService.login(request);

            User user = authService.getAuthenticatedUser()
                    .orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

            JwtResponse jwtResponse = new JwtResponse(user.getId(), jwt);
            return ResponseEntity.ok(Response.success(jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error(e.getMessage()));
        }
    }


    // 리프레시 토큰으로 새로운 액세스 토큰 발급
//    @PostMapping("/refresh-token")
//    public ResponseEntity<ApiResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
//        try {
//            String newAccessToken = jwtProvider.refreshAccessToken(request.getRefreshToken());
//            return ResponseEntity.ok(new ApiResponse("Token refreshed successfully", newAccessToken));
//        } catch (JwtException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ApiResponse("Failed to refresh token", null));
//        }
//    }

}
