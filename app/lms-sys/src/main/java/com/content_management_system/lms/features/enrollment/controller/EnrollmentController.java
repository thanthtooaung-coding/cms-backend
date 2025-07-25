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
@RequestMapping("/api/v1/lms/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(@RequestBody CreateEnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> responses = enrollmentService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EnrollmentResponse>> searchEnrollments(
            @RequestParam(required = false) String studentEmail,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long categoryId) {
        List<EnrollmentResponse> responses = enrollmentService.search(studentEmail, courseId, categoryId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}