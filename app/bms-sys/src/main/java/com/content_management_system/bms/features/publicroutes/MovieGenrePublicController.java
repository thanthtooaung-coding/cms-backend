package com.content_management_system.bms.features.publicroutes;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import com.content_management_system.bms.features.moviegenres.service.MovieGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/movie-genres")
@RequiredArgsConstructor
public class MovieGenrePublicController {

    private final MovieGenreService movieGenreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieGenreResponse>>> retrieveAll() {
        return ApiResponse.ok(this.movieGenreService.retrieveAll(), "Movie genres retrieved successfully");
    }
}
