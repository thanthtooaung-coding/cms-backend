package com.content_management_system.lms.features.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentStatisticsResponse {
    private Long courseId;
    private String courseName;
    private Long instructorId;
    private String instructorName;
    private Long studentCount;
    private Long certifiedStudentCount;
}

