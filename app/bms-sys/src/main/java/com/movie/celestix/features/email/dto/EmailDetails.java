package com.movie.celestix.features.email.dto;

import java.math.BigDecimal;

public record EmailDetails(
        String customerName,
        String customerEmail,
        String bookingId,
        String movieTitle,
        String theaterName,
        String showtimeDate,
        String showtimeTime,
        String seats,
        BigDecimal totalAmount,
        String tenantName,
        String tenantLogoUrl
) {}
