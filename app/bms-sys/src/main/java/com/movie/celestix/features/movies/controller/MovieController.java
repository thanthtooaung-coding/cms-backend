package com.movie.celestix.features.movies.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.movies.dto.CreateMovieRequest;
import com.movie.celestix.features.movies.dto.MovieResponse;
import com.movie.celestix.features.movies.dto.UpdateMovieRequest;
import com.movie.celestix.features.movies.dto.MovieTemplateResponse;
import com.movie.celestix.features.movies.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> create(
            @RequestBody final CreateMovieRequest request,
            @RequestParam Long tenantId
    ) {
        final MovieResponse createdMovie = movieService.create(request, tenantId);
        return ApiResponse.created(createdMovie, "Movie created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> retrieveOne(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(movieService.retrieveOne(id, tenantId), "Movie retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(movieService.retrieveAll(tenantId), "Movies retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateMovieRequest request,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(movieService.update(id, request, tenantId), "Movie updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        movieService.delete(id, tenantId);
        return ApiResponse.noContent("Movie deleted successfully");
    }

    @GetMapping("/template")
    public ResponseEntity<ApiResponse<MovieTemplateResponse>> getMovieTemplate() {
        return ApiResponse.ok(movieService.getMovieTemplate(), "Movie template data retrieved successfully");
    }
}
