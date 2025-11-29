package com.movie.celestix.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "admin@celestix.com")
    String email,
    @Schema(example = "admin123")
    String password,
    @Schema(example = "1")
    Long tenantId
) {}
