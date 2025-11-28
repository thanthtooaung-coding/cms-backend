package com.content_management_system.lms.features.certificate.controller;

import com.content_management_system.lms.features.certificate.dto.CertificateResponse;
import com.content_management_system.lms.features.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<CertificateResponse> getCertificate(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CertificateResponse certificate = certificateService.getCertificate(studentId, courseId);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CertificateResponse>> getAllCertificatesByStudent(
            @PathVariable Long studentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<CertificateResponse> certificates = certificateService.getAllCertificatesByStudent(studentId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/student/{studentId}/course/{courseId}/eligible")
    public ResponseEntity<Boolean> checkEligibility(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        boolean eligible = certificateService.isEligibleForCertificate(studentId, courseId);
        return ResponseEntity.ok(eligible);
    }
}

