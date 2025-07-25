package com.content_management_system.lms.features.enrollment.service;

import com.content_management_system.lms.features.enrollment.dto.CreateEnrollmentRequest;
import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse create(CreateEnrollmentRequest request);

    List<EnrollmentResponse> findAll();

    List<EnrollmentResponse> search(String studentEmail, Long courseId, Long categoryId);

    void deleteById(Long id);

    void cancelEnrollment(Long id);
}