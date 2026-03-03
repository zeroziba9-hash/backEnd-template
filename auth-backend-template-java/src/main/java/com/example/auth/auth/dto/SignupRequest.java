package com.example.auth.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @Size(max = 50) String name
) {}
