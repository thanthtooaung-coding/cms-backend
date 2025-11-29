package com.movie.celestix.features.admins.dto;

public record CreateAdminRequest(
        String name,
        String email,
        String password,
        Long tenantId
) {}
