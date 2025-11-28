package com.content_management_system.lms.features.course.dto;

import com.content_management_system.lms.shared.constants.CourseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private CourseStatus status;
    private Integer durationDayCount;
    private CategoryInfo category;
    private InstructorInfo instructor;
    private List<ModuleInfo> modules;
    private RatingInfo rating;
    private Integer totalEnrolledStudents;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<WhatYouWillLearnItem> whatYouWillLearn;
    private List<RequirementItem> requirements;

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
        private Integer totalCourses;
        private Integer totalStudents;
    }

    @Data
    @Builder
    public static class ModuleInfo {
        private Long id;
        private String name;
        private String description;
        private List<LessonInfo> lessons;
    }

    @Data
    @Builder
    public static class LessonInfo {
        private Long id;
        private String title;
        private String content;
        private String materialType;
    }

    @Data
    @Builder
    public static class RatingInfo {
        private Double averageRating;
        private Integer totalRatings;
    }

    @Data
    @Builder
    public static class WhatYouWillLearnItem {
        private Long id;
        private String text;
    }

    @Data
    @Builder
    public static class RequirementItem {
        private Long id;
        private String text;
    }
}