package com.content_management_system.bms.features.auth.dto;

import com.content_management_system.bms.common.enums.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        String profileUrl
) {}
