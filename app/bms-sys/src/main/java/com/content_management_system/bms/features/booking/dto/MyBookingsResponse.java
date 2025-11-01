package com.content_management_system.bms.features.booking.dto;

import java.util.List;

public record MyBookingsResponse(
        List<BookingDetailResponse> upcoming,
        List<BookingDetailResponse> completed
) {}
