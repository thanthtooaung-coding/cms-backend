package com.content_management_system.lms.features.enrollment.service;

import com.content_management_system.lms.features.enrollment.dto.CreateEnrollmentRequest;
import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;
import com.content_management_system.lms.features.enrollment.dto.EnrolledCourseResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse create(CreateEnrollmentRequest request, Long userId);

    List<EnrollmentResponse> findAll(Long tenantId, Long userId);

    List<EnrollmentResponse> search(String studentEmail, Long courseId, Long categoryId, Long userId);

    void deleteById(Long id, Long userId);

    void cancelEnrollment(Long id, Long userId);

    boolean isStudentEnrolled(Long studentId, Long courseId);
    
    List<EnrolledCourseResponse> getEnrolledCourses(Long studentId, Long userId);
    
    List<com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse> getEnrollmentStatistics(Long tenantId, Long userId);
}