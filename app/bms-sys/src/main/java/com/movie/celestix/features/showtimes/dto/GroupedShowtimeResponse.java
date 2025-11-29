package com.movie.celestix.features.showtimes.dto;

import java.util.List;

public record GroupedShowtimeResponse(
        MovieInfo movie,
        List<TheaterWithShowtimes> theaters
) {}