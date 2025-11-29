package com.movie.celestix.features.refund.dto;

public record RefundResponse(
        Long id,
        String bookingId,
        String customerName,
        String customerEmail,
        String approvedAdminName,
        String approvedAdminEmail,
        String status
) {}
