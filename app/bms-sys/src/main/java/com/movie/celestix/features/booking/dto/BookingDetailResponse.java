package com.movie.celestix.features.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingDetailResponse(
        Long id,
        String bookingId,
        String customerName,
        String customerEmail,
        String movieTitle,
        LocalDate bookingDate,
        String bookingTime,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        String seats,
        BigDecimal totalAmount,
        String status,
        String paymentStatus,
        String theaterName,
        boolean isAlreadyRequestRefund
) {}
