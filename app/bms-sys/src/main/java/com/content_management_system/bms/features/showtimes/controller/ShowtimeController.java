package com.content_management_system.bms.features.showtimes.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.showtimes.dto.*;
import com.content_management_system.bms.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(@RequestBody CreateShowtimeRequest request) {
        ShowtimeResponse createdShowtime = showtimeService.create(request);
        return ApiResponse.created(createdShowtime, "Showtime created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> retrieveOne(@PathVariable Long id) {
        return ApiResponse.ok(showtimeService.retrieveOne(id), "Showtime retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> retrieveAll() {
        return ApiResponse.ok(showtimeService.retrieveAll(), "Showtimes retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(@PathVariable Long id, @RequestBody UpdateShowtimeRequest request) {
        return ApiResponse.ok(showtimeService.update(id, request), "Showtime updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        showtimeService.delete(id);
        return ApiResponse.noContent("Showtime deleted successfully");
    }

    @GetMapping("/template")
    public ResponseEntity<ApiResponse<ShowtimeTemplateResponse>> getShowtimeTemplate() {
        return ApiResponse.ok(showtimeService.getShowtimeTemplate(), "Showtime template data retrieved successfully");
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ApiResponse<List<GroupedShowtimeResponse>>> getShowtimesByMovie(
            @PathVariable Long movieId) {
        return ApiResponse.ok(
                showtimeService.retrieveByMovieId(movieId),
                "Showtimes for movie retrieved successfully"
        );
    }

    @GetMapping("/check-conflicts")
    public ResponseEntity<ApiResponse<List<ShowtimeConflictResponse>>> checkConflicts(@RequestParam int newInterval) {
        return ApiResponse.ok(
                showtimeService.findConflictingShowtimes(newInterval),
                "Conflict check completed successfully"
        );
    }
}
