package com.movie.celestix.features.showtimes.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ShowtimeResponse(
        Long id,
        MovieInfo movie,
        TheaterInfo theater,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        Integer seatsAvailable,
        String status,
        List<String> bookedSeats
) {}
