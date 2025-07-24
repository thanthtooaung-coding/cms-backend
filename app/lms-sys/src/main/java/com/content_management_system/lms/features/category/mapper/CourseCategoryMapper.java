package com.content_management_system.lms.features.category.mapper;

import com.content_management_system.lms.features.category.dto.CourseCategoryResponse;
import com.content_management_system.lms.shared.entity.CourseCategory;

public class CourseCategoryMapper {
    public static CourseCategoryResponse toResponse(CourseCategory courseCategory) {
    	CourseCategoryResponse.TenantInfo tenantInfo = null;
        if (courseCategory.getTenant() != null) {
            tenantInfo = CourseCategoryResponse.TenantInfo.builder()
                    .id(courseCategory.getTenant().getId())
                    .name(courseCategory.getTenant().getName())
                    .build();
        }
        return CourseCategoryResponse.builder()
                .id(courseCategory.getId())
                .name(courseCategory.getName())
                .description(courseCategory.getDescription())
                .tenant(tenantInfo)
                .build();
    }
}