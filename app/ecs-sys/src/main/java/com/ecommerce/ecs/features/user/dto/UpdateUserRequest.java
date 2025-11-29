package com.ecommerce.ecs.features.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private Long roleId;
    private String address;
    private String phoneNumber;
    private String profileUrl;
}

