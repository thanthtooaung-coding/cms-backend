package com.content_management_system.lms.features.certificate.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class CertificateResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private String certificateNumber;
    private OffsetDateTime issuedDate;
    private BigDecimal scorePercentage;
}

