package com.content_management_system.bms.features.showtimes.dto;

public record ShowtimeDetail(
        Long id,
        java.time.LocalDate showtimeDate,
        java.time.LocalTime showtimeTime,
        Integer seatsAvailable,
        String status
) {}
