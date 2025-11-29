package com.movie.celestix.features.moviegenres.service;

import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;
import com.movie.celestix.features.moviegenres.dto.CreateMovieGenreRequest;
import com.movie.celestix.features.moviegenres.dto.UpdateMovieGenreRequest;

import java.util.List;

public interface MovieGenreService {
    MovieGenreResponse create(CreateMovieGenreRequest request, Long tenantId);
    MovieGenreResponse retrieveOne(Long id, Long tenantId);
    List<MovieGenreResponse> retrieveAll(Long tenantId);
    MovieGenreResponse update(Long id, UpdateMovieGenreRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}
