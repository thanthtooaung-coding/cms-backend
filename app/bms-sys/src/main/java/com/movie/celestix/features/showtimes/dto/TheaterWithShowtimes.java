package com.movie.celestix.features.showtimes.dto;

import java.util.List;

public record TheaterWithShowtimes(
        TheaterInfo theater,
        List<ShowtimeDetail> showtimes
) {}