package com.movie.celestix.features.showtimes.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeConflictResponse(
    String movieTitle,
    String theaterName,
    LocalDate showtimeDate,
    LocalTime showtimeTime
) {}
