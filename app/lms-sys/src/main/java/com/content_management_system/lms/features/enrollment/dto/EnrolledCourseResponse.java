package com.content_management_system.lms.features.enrollment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class EnrolledCourseResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String categoryName;
    private Long categoryId;
    private String instructorName;
    private Long instructorId;
    private String instructorEmail;
    private OffsetDateTime enrollmentDate;
    private String enrollmentStatus;
    private Integer totalModules;
    private Integer totalLessons;
    private Integer totalQuizzes;
    private Double averageRating;
    private Integer totalRatings;
    private Integer totalEnrolledStudents;
    private OffsetDateTime courseCreatedAt;
    private OffsetDateTime courseUpdatedAt;
    private String courseStatus;
    private Integer durationDayCount;
    private Boolean hasCertificate;
    private Double certificateScore;
}

