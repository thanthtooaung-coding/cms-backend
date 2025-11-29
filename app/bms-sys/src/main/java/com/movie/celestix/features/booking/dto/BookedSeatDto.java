package com.movie.celestix.features.booking.dto;

import java.math.BigDecimal;

public record BookedSeatDto(
        Long id,
        String seatNumber,
        BigDecimal price
) {}
