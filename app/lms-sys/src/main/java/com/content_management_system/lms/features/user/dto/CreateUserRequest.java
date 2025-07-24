package com.content_management_system.lms.features.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private Long roleId;
    private Long tenantId;
}