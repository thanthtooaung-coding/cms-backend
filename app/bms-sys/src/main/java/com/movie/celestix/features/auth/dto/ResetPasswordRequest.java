package com.movie.celestix.features.auth.dto;

public record ResetPasswordRequest(
        String email,
        String otp,
        String newPassword
) {}
