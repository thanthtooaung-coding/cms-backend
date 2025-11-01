package com.content_management_system.bms.features.admins.dto;

public record CreateAdminRequest(
        String name,
        String email,
        String password
) {}
