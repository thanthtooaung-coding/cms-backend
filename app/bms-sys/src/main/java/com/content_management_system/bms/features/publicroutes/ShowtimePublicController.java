package com.content_management_system.bms.features.publicroutes;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.showtimes.dto.GroupedShowtimeResponse;
import com.content_management_system.bms.features.showtimes.dto.ShowtimeResponse;
import com.content_management_system.bms.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/showtimes")
@RequiredArgsConstructor
public class ShowtimePublicController {
    private final ShowtimeService showtimeService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> retrieveOne(@PathVariable Long id) {
        return ApiResponse.ok(showtimeService.retrieveOne(id), "Showtime retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> retrieveAll() {
        return ApiResponse.ok(showtimeService.retrieveAll(), "Showtimes retrieved successfully");
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<GroupedShowtimeResponse>>> retrieveAllGroupByMovieAndTheater(
            @RequestParam(required = false) boolean retrieveAll
    ) {
        return ApiResponse.ok(showtimeService.retrieveAllGroupByMovieAndTheater(retrieveAll),
                "Showtimes grouped by movie and theater retrieved successfully");
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ApiResponse<List<GroupedShowtimeResponse>>> getShowtimesByMovie(
            @PathVariable Long movieId) {
        return ApiResponse.ok(
                showtimeService.retrieveByMovieId(movieId),
                "Showtimes for movie retrieved successfully"
        );
    }
}