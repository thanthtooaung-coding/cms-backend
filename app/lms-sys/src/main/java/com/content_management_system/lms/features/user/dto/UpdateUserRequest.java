package com.content_management_system.lms.features.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private String name;
    private String address;
    private String phoneNumber;
    private Long roleId;
}