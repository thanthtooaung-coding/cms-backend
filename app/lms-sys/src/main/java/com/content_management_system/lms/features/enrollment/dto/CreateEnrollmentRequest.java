package com.content_management_system.lms.features.enrollment.dto;

import lombok.Data;

@Data
public class CreateEnrollmentRequest {
    private Long studentId;
    private Long courseId;
}