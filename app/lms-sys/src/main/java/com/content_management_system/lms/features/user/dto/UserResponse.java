package com.content_management_system.lms.features.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private RoleInfo role;
    private TenantInfo tenant;

    @Data
    @Builder
    public static class RoleInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class TenantInfo {
        private Long id;
        private String name;
    }
}