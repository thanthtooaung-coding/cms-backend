package com.content_management_system.lms.features.enrollment.controller;

import com.content_management_system.lms.features.enrollment.dto.CreateEnrollmentRequest;
import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;
import com.content_management_system.lms.features.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @RequestBody CreateEnrollmentRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        EnrollmentResponse response = enrollmentService.create(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments(
            @RequestParam(required = false) Long tenantId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<EnrollmentResponse> responses = enrollmentService.findAll(tenantId, userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EnrollmentResponse>> searchEnrollments(
            @RequestParam(required = false) String studentEmail,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long categoryId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<EnrollmentResponse> responses = enrollmentService.search(studentEmail, courseId, categoryId, userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        enrollmentService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEnrollment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        enrollmentService.cancelEnrollment(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{studentId}/{courseId}")
    public ResponseEntity<java.util.Map<String, Boolean>> checkEnrollment(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        boolean enrolled = enrollmentService.isStudentEnrolled(studentId, courseId);
        return ResponseEntity.ok(java.util.Map.of("enrolled", enrolled));
    }

    @GetMapping("/student/{studentId}/courses")
    public ResponseEntity<List<com.content_management_system.lms.features.enrollment.dto.EnrolledCourseResponse>> getEnrolledCourses(
            @PathVariable Long studentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<com.content_management_system.lms.features.enrollment.dto.EnrolledCourseResponse> courses = enrollmentService.getEnrolledCourses(studentId, userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse>> getEnrollmentStatistics(
            @RequestParam(required = false) Long tenantId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse> statistics = enrollmentService.getEnrollmentStatistics(tenantId, userId);
        return ResponseEntity.ok(statistics);
    }
}