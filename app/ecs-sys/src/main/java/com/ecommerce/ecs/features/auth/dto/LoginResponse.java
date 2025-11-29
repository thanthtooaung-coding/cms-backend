package com.ecommerce.ecs.features.auth.dto;

import com.ecommerce.ecs.common.enums.Role;

public record LoginResponse(
    String token,
    Role role,
    Long userId,
    String name,
    String email
) {}

