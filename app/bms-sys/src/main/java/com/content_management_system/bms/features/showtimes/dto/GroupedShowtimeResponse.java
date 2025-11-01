package com.content_management_system.bms.features.showtimes.dto;

import java.util.List;

public record GroupedShowtimeResponse(
        MovieInfo movie,
        List<TheaterWithShowtimes> theaters
) {}