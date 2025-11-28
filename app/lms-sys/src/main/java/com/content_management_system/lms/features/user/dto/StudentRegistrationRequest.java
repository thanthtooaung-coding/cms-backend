package com.content_management_system.lms.features.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentRegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private Long tenantId; // Optional, can be determined from tenant slug
}

