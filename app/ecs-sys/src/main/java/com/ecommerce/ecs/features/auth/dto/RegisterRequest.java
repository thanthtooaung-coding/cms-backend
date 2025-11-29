package com.ecommerce.ecs.features.auth.dto;

import com.ecommerce.ecs.common.enums.Role;
import lombok.Data;

public record RegisterRequest(
    String username,
    String email,
    String password,
    String name,
    String address,
    String phoneNumber,
    Long roleId,
    Long tenantId
) {}

