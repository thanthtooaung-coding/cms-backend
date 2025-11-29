package com.movie.celestix.features.publicroutes;

import com.cloudinary.utils.StringUtils;
import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.movies.dto.MovieResponse;
import com.movie.celestix.features.movies.service.MovieService;
import com.movie.celestix.features.publicroutes.dto.PopularMovieResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/public/movies")
@RequiredArgsConstructor
public class MoviePublicController {
    private final MovieService movieService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> retrieveOne(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(movieService.retrieveOne(id, tenantId), "Movie retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> retrieveAll(
            @Parameter(
                    description = "Filter movies by status",
                    schema = @Schema(allowableValues = {"Now Showing", "Coming Soon", "all"})
            )
            @RequestParam(required = false) String status,
            @RequestParam Long tenantId
    ) {
        List<MovieResponse> movieResponseList;
        if (StringUtils.isBlank(status) || status.equalsIgnoreCase("all") ) {
            movieResponseList = movieService.retrieveAvailableMovies(tenantId);
        } else {
            movieResponseList = movieService.retrieveAllAvailableMoviesByStatus(status, tenantId);
        }
        return ApiResponse.ok(
                movieResponseList
                , "Movies retrieved successfully");
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularMovieResponse>>> retrievePopularMovies(
            @RequestParam Long tenantId
    ) {
        List<PopularMovieResponse> popularMovies = movieService.retrievePopularMovies(tenantId);
        return ApiResponse.ok(popularMovies, "Popular movies retrieved successfully");
    }
}
