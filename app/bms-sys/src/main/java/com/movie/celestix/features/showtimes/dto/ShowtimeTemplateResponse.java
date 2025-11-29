package com.movie.celestix.features.showtimes.dto;

import com.movie.celestix.features.movies.dto.EnumResponse;
import java.util.List;

public record ShowtimeTemplateResponse(
        List<MovieTemplateData> movies,
        List<TheaterTemplateData> theaters,
        List<EnumResponse> statusOptions
) {}
