package com.movie.celestix.features.movies.dto;

import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;

import java.time.LocalDate;
import java.util.Set;

public record MovieResponse(
        Long id,
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
        Set<MovieGenreResponse> genres
) {}
