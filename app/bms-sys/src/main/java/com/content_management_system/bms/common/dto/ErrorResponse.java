package com.content_management_system.bms.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Standard error response")
public record ErrorResponse(
        int status,
        String message
) {}
