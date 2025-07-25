package com.content_management_system.lms.features.module.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleResponse {

    private String name;
    private String description;
    private CourseInfo course;

    @Data
    @Builder
    public static class CourseInfo {
        private Long id;
        private String name;
    }
}