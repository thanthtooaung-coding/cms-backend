package com.content_management_system.bms.features.moviegenres.service;

import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import com.content_management_system.bms.features.moviegenres.dto.CreateMovieGenreRequest;
import com.content_management_system.bms.features.moviegenres.dto.UpdateMovieGenreRequest;

import java.util.List;

public interface MovieGenreService {
    MovieGenreResponse create(CreateMovieGenreRequest request);
    MovieGenreResponse retrieveOne(Long id);
    List<MovieGenreResponse> retrieveAll();
    MovieGenreResponse update(Long id, UpdateMovieGenreRequest request);
    void delete(Long id);
}
