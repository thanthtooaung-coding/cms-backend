package com.movie.celestix.features.showtimes.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.showtimes.dto.*;
import com.movie.celestix.features.showtimes.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(
            @RequestBody CreateShowtimeRequest request,
            @RequestParam Long tenantId
    ) {
        ShowtimeResponse createdShowtime = showtimeService.create(request, tenantId);
        return ApiResponse.created(createdShowtime, "Showtime created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> retrieveOne(
            @PathVariable Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(showtimeService.retrieveOne(id, tenantId), "Showtime retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(showtimeService.retrieveAll(tenantId), "Showtimes retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateShowtimeRequest request,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(showtimeService.update(id, request, tenantId), "Showtime updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long tenantId
    ) {
        showtimeService.delete(id, tenantId);
        return ApiResponse.noContent("Showtime deleted successfully");
    }

    @GetMapping("/template")
    public ResponseEntity<ApiResponse<ShowtimeTemplateResponse>> getShowtimeTemplate(@RequestParam Long tenantId) {
        return ApiResponse.ok(showtimeService.getShowtimeTemplate(tenantId), "Showtime template data retrieved successfully");
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ApiResponse<List<GroupedShowtimeResponse>>> getShowtimesByMovie(
            @PathVariable Long movieId,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(
                showtimeService.retrieveByMovieId(movieId, tenantId),
                "Showtimes for movie retrieved successfully"
        );
    }

    @GetMapping("/check-conflicts")
    public ResponseEntity<ApiResponse<List<ShowtimeConflictResponse>>> checkConflicts(
            @RequestParam int newInterval,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(
                showtimeService.findConflictingShowtimes(newInterval, tenantId),
                "Conflict check completed successfully"
        );
    }
}
