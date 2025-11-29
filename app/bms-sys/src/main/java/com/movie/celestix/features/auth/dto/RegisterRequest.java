package com.movie.celestix.features.auth.dto;

import com.movie.celestix.common.enums.Role;
import lombok.Data;

public record RegisterRequest (
    String name,
    String email,
    String password,
    Role role
) {}
