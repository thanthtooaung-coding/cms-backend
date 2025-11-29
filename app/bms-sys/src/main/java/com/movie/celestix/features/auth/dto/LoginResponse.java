package com.movie.celestix.features.auth.dto;

import com.movie.celestix.common.enums.Role;

public record LoginResponse(
        String token,
        Role role
) {}
