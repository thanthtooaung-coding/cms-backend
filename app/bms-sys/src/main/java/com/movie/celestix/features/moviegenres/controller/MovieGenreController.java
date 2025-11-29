package com.movie.celestix.features.moviegenres.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;
import com.movie.celestix.features.moviegenres.dto.CreateMovieGenreRequest;
import com.movie.celestix.features.moviegenres.dto.UpdateMovieGenreRequest;
import com.movie.celestix.features.moviegenres.service.MovieGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie-genres")
@RequiredArgsConstructor
public class MovieGenreController {

    private final MovieGenreService movieGenreService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieGenreResponse>> create(
            @RequestBody final CreateMovieGenreRequest request,
            @RequestParam Long tenantId
    ) {
        final MovieGenreResponse createdGenre = this.movieGenreService.create(request, tenantId);
        return ApiResponse.created(createdGenre, "Movie genre created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieGenreResponse>> retrieveOne(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(this.movieGenreService.retrieveOne(id, tenantId), "Movie genre retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieGenreResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(this.movieGenreService.retrieveAll(tenantId), "Movie genres retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieGenreResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateMovieGenreRequest request,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(this.movieGenreService.update(id, request, tenantId), "Movie genre updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        this.movieGenreService.delete(id, tenantId);
        return ApiResponse.noContent("Movie genre deleted successfully");
    }
}
