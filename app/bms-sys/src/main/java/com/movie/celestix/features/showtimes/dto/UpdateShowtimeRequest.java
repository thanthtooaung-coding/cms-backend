package com.movie.celestix.features.showtimes.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateShowtimeRequest(
        Long movieId,
        Long theaterId,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        String status
) {}
