package com.movie.celestix.features.booking.dto;

import java.util.List;

public record MyBookingsResponse(
        List<BookingDetailResponse> upcoming,
        List<BookingDetailResponse> completed
) {}
