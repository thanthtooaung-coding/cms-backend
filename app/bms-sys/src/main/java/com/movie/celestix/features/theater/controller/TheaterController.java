package com.movie.celestix.features.theater.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.common.dto.ErrorResponse;
import com.movie.celestix.features.theater.dto.CreateTheaterRequest;
import com.movie.celestix.features.theater.dto.TheaterResponse;
import com.movie.celestix.features.theater.dto.UpdateTheaterRequest;
import com.movie.celestix.features.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @PostMapping
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Theater created successfully",
                    content = @Content(schema = @Schema(implementation = TheaterResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Theater already exists or seat configuration invalid",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Duplicate Theater",
                                            value = """
                                                {
                                                  "status": 409,
                                                  "message": "Theater with name 'IMAX' already exists"
                                                }
                                                """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Seat Configuration",
                                            value = """
                                                {
                                                  "status": 409,
                                                  "message": "Total number of rows in seat configuration does not match the sum of rows for all seat types."
                                                }
                                                """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<TheaterResponse>> create(
            @RequestBody final CreateTheaterRequest request,
            @RequestParam Long tenantId
    ) {
        final TheaterResponse createdTheater = theaterService.create(request, tenantId);
        return ApiResponse.created(createdTheater, "Theater created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> retrieveOne(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(theaterService.retrieveOne(id, tenantId), "Theater retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(theaterService.retrieveAll(tenantId), "Theaters retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateTheaterRequest request,
            @RequestParam Long tenantId
    ) {
        return ApiResponse.ok(theaterService.update(id, request, tenantId), "Theater updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        theaterService.delete(id, tenantId);
        return ApiResponse.noContent("Theater deleted successfully");
    }
}
