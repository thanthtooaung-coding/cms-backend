package com.content_management_system.lms.features.course.dto;

import com.content_management_system.lms.shared.constants.CourseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCourseRequest {
    private String title;
    private String description;
    private Long categoryId;
    private Long instructorId;
    private Integer durationDayCount;
    private CourseStatus status;
}