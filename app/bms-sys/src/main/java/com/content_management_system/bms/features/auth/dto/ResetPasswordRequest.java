package com.content_management_system.bms.features.auth.dto;

public record ResetPasswordRequest(
        String email,
        String otp,
        String newPassword
) {}
