package com.movie.celestix.features.movies.service;

import com.movie.celestix.features.movies.dto.CreateMovieRequest;
import com.movie.celestix.features.movies.dto.MovieResponse;
import com.movie.celestix.features.movies.dto.MovieTemplateResponse;
import com.movie.celestix.features.movies.dto.UpdateMovieRequest;
import com.movie.celestix.features.publicroutes.dto.PopularMovieResponse;

import java.util.List;

public interface MovieService {
    MovieResponse create(CreateMovieRequest request, Long tenantId);
    MovieResponse retrieveOne(Long id, Long tenantId);
    List<MovieResponse> retrieveAll(Long tenantId);
    MovieResponse update(Long id, UpdateMovieRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
    MovieTemplateResponse getMovieTemplate();
    List<MovieResponse> retrieveAllByStatus(String status, Long tenantId);
    List<MovieResponse> retrieveAvailableMovies(Long tenantId);
    List<MovieResponse> retrieveAllAvailableMoviesByStatus(String status, Long tenantId);
    List<PopularMovieResponse> retrievePopularMovies(Long tenantId);
}
