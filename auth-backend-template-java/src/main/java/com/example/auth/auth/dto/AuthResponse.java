package com.example.auth.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, UserResponse user) {}
