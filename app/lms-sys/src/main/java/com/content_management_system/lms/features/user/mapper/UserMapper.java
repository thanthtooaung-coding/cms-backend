package com.content_management_system.lms.features.user.mapper;

import com.content_management_system.lms.features.user.dto.UserResponse;
import com.content_management_system.lms.shared.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        UserResponse.RoleInfo roleInfo = null;
        if (user.getRole() != null) {
            roleInfo = UserResponse.RoleInfo.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName().name().toLowerCase())
                    .build();
        }

        UserResponse.TenantInfo tenantInfo = null;
        if (user.getTenant() != null) {
            tenantInfo = UserResponse.TenantInfo.builder()
                    .id(user.getTenant().getId())
                    .name(user.getTenant().getName())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .role(roleInfo)
                .tenant(tenantInfo)
                .build();
    }
}