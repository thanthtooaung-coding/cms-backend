package com.content_management_system.lms.features.enrollment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnrollmentResponse {

    private Long id;
    private StudentInfo student;
    private CourseInfo course;
    private CategoryInfo category;

    @Data
    @Builder
    public static class StudentInfo {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    public static class CourseInfo {
        private Long id;
        private String title;
    }

    @Data
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String name;
    }
}