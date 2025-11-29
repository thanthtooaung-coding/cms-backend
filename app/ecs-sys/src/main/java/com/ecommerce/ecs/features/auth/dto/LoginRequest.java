package com.ecommerce.ecs.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "customer@example.com")
    String email,
    @Schema(example = "password123")
    String password,
    @Schema(example = "1")
    Long tenantId
) {}

