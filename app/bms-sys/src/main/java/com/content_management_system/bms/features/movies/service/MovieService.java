package com.content_management_system.bms.features.movies.service;

import com.content_management_system.bms.features.movies.dto.CreateMovieRequest;
import com.content_management_system.bms.features.movies.dto.MovieResponse;
import com.content_management_system.bms.features.movies.dto.MovieTemplateResponse;
import com.content_management_system.bms.features.movies.dto.UpdateMovieRequest;
import com.content_management_system.bms.features.publicroutes.dto.PopularMovieResponse;

import java.util.List;

public interface MovieService {
    MovieResponse create(CreateMovieRequest request);
    MovieResponse retrieveOne(Long id);
    List<MovieResponse> retrieveAll();
    MovieResponse update(Long id, UpdateMovieRequest request);
    void delete(Long id);
    MovieTemplateResponse getMovieTemplate();
    List<MovieResponse> retrieveAllByStatus(String status);
    List<MovieResponse> retrieveAvailableMovies();
    List<MovieResponse> retrieveAllAvailableMoviesByStatus(String status);
    List<PopularMovieResponse> retrievePopularMovies();
}
