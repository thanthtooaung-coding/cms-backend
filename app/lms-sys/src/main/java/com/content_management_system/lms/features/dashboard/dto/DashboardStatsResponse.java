package com.content_management_system.lms.features.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalCourses;
    private Long totalStudents;
    private Long totalInstructors;
    private Long totalEnrollments;
    private Long totalCertificates;
    private Long totalCategories;
}

