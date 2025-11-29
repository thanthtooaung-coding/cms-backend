package com.movie.celestix.features.booking.dto;

import com.movie.celestix.common.enums.BookingStatus;
import com.movie.celestix.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Set;

public record BookingResponse(
        Long id,
        String bookingId,
        Long userId,
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus,
        Set<BookedSeatDto> bookedSeats,
        BigDecimal totalPrice
) {}
