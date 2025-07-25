package com.content_management_system.lms.features.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCourseRequest {
    private String title;
    private String description;
    private Long categoryId;
    private Long instructorId;
    private Integer durationDayCount;
}