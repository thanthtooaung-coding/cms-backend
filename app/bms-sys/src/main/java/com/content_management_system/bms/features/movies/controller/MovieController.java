package com.content_management_system.bms.features.movies.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.movies.dto.CreateMovieRequest;
import com.content_management_system.bms.features.movies.dto.MovieResponse;
import com.content_management_system.bms.features.movies.dto.UpdateMovieRequest;
import com.content_management_system.bms.features.movies.dto.MovieTemplateResponse;
import com.content_management_system.bms.features.movies.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> create(@RequestBody final CreateMovieRequest request) {
        final MovieResponse createdMovie = movieService.create(request);
        return ApiResponse.created(createdMovie, "Movie created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> retrieveOne(@PathVariable final Long id) {
        return ApiResponse.ok(movieService.retrieveOne(id), "Movie retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> retrieveAll() {
        return ApiResponse.ok(movieService.retrieveAll(), "Movies retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateMovieRequest request
    ) {
        return ApiResponse.ok(movieService.update(id, request), "Movie updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final Long id) {
        movieService.delete(id);
        return ApiResponse.noContent("Movie deleted successfully");
    }

    @GetMapping("/template")
    public ResponseEntity<ApiResponse<MovieTemplateResponse>> getMovieTemplate() {
        return ApiResponse.ok(movieService.getMovieTemplate(), "Movie template data retrieved successfully");
    }
}
