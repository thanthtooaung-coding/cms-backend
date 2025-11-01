package com.content_management_system.bms.features.publicroutes.dto;

import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import java.time.LocalDate;
import java.util.Set;

public record PopularMovieResponse(
        Long id,
        String title,
        String description,
        String duration,
        LocalDate releaseDate,
        String moviePosterUrl,
        String trailerUrl,
        Set<MovieGenreResponse> genres,
        Double popularityRating,
        String rating
) {}
