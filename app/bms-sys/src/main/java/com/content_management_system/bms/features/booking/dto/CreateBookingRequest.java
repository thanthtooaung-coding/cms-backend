package com.content_management_system.bms.features.booking.dto;

import java.util.List;

public record CreateBookingRequest(
        Long showtimeId,
        List<String> seatNumbers,
        CardDetails cardDetails
) {}
