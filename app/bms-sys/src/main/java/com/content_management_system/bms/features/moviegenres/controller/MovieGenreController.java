package com.content_management_system.bms.features.moviegenres.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import com.content_management_system.bms.features.moviegenres.dto.CreateMovieGenreRequest;
import com.content_management_system.bms.features.moviegenres.dto.UpdateMovieGenreRequest;
import com.content_management_system.bms.features.moviegenres.service.MovieGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie-genres")
@RequiredArgsConstructor
public class MovieGenreController {

    private final MovieGenreService movieGenreService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieGenreResponse>> create(@RequestBody final CreateMovieGenreRequest request) {
        final MovieGenreResponse createdGenre = this.movieGenreService.create(request);
        return ApiResponse.created(createdGenre, "Movie genre created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieGenreResponse>> retrieveOne(@PathVariable final Long id) {
        return ApiResponse.ok(this.movieGenreService.retrieveOne(id), "Movie genre retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieGenreResponse>>> retrieveAll() {
        return ApiResponse.ok(this.movieGenreService.retrieveAll(), "Movie genres retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieGenreResponse>> update(@PathVariable final Long id, @RequestBody final UpdateMovieGenreRequest request) {
        return ApiResponse.ok(this.movieGenreService.update(id, request), "Movie genre updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final Long id) {
        this.movieGenreService.delete(id);
        return ApiResponse.noContent("Movie genre deleted successfully");
    }
}
