package com.content_management_system.lms.features.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private CategoryInfo category;
    private InstructorInfo instructor;

    @Data
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @Builder
    public static class InstructorInfo {
        private Long id;
        private String name;
        private String email;
    }
}