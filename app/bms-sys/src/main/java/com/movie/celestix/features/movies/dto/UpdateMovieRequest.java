package com.movie.celestix.features.movies.dto;

import java.time.LocalDate;
import java.util.Set;

public record UpdateMovieRequest(
        String title,
        String description,
        String duration,
        LocalDate releaseDate,
        String rating,
        String language,
        String director,
        String movieCast,
        String trailerUrl,
        String moviePosterUrl,
        String status,
        Set<Long> genreIds
) {}
