package com.content_management_system.bms.features.auth.dto;

import com.content_management_system.bms.common.enums.Role;

public record RegisterRequest (
    String name,
    String email,
    String password,
    Role role
) {}
