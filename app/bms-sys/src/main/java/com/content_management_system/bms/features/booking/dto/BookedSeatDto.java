package com.content_management_system.bms.features.booking.dto;

import java.math.BigDecimal;

public record BookedSeatDto(
        Long id,
        String seatNumber,
        BigDecimal price
) {}
