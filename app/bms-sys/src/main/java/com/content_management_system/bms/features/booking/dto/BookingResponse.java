package com.content_management_system.bms.features.booking.dto;

import com.content_management_system.bms.common.enums.BookingStatus;
import com.content_management_system.bms.common.enums.PaymentStatus;

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
