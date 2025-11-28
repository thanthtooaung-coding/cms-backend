package com.content_management_system.lms.features.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private Long tenantId;
}

