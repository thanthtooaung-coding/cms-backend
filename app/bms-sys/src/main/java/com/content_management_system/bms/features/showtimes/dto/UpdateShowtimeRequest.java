package com.content_management_system.bms.features.showtimes.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateShowtimeRequest(
        Long movieId,
        Long theaterId,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        String status
) {}
