package com.content_management_system.lms.features.module.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateModuleRequest {
    private String name;
    private String description;
}