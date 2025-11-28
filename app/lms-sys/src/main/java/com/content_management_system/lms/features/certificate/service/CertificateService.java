package com.content_management_system.lms.features.certificate.service;

import com.content_management_system.lms.features.certificate.dto.CertificateResponse;

import java.util.List;

public interface CertificateService {
    CertificateResponse generateCertificateIfEligible(Long studentId, Long courseId);
    
    CertificateResponse getCertificate(Long studentId, Long courseId);
    
    List<CertificateResponse> getAllCertificatesByStudent(Long studentId);
    
    boolean isEligibleForCertificate(Long studentId, Long courseId);
}

