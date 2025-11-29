package com.movie.celestix.features.auth.dto;

import com.movie.celestix.common.enums.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        String profileUrl
) {}
