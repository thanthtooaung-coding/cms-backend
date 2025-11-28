package com.content_management_system.lms.features.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String address;
    private String phoneNumber;
}

