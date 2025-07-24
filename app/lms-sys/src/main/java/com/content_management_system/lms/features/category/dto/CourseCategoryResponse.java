package com.content_management_system.lms.features.category.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CourseCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private TenantInfo tenant;
    
    @Data
    @Builder
    public static class TenantInfo {
        private Long id;
        private String name;
    }
}