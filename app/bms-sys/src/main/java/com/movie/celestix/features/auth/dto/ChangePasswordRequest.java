package com.movie.celestix.features.auth.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}

