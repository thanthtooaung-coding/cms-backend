package com.content_management_system.bms.features.showtimes.dto;

import java.util.List;

public record TheaterWithShowtimes(
        TheaterInfo theater,
        List<ShowtimeDetail> showtimes
) {}