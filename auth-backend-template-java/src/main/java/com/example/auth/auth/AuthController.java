package com.example.auth.auth;

import com.example.auth.auth.dto.*;
import com.example.auth.common.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health")
    public ApiResponse<Boolean> health() {
        return ApiResponse.ok(true);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@RequestBody @Valid SignupRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(authService.signup(req)));
    }

    @PostMapping("/auth/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest req,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        AuthResponse auth = authService.login(req, request);
        setRefreshCookie(response, auth.refreshToken());
        return ApiResponse.ok(auth);
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody(required = false) RefreshRequest req,
                                 HttpServletRequest request) {
        String token = pickRefreshToken(req != null ? req.refreshToken() : null, request);
        return ApiResponse.ok(authService.refresh(token));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshRequest req,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        String token = pickRefreshToken(req != null ? req.refreshToken() : null, request);
        authService.logout(token);
        clearRefreshCookie(response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok());
    }

    @GetMapping("/auth/me")
    public ApiResponse<UserResponse> me(@RequestHeader(name = "Authorization", required = false) String authorization) {
        return ApiResponse.ok(authService.me(authorization));
    }

    private String pickRefreshToken(String bodyToken, HttpServletRequest request) {
        if (bodyToken != null && !bodyToken.isBlank()) return bodyToken;
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 3600);
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
