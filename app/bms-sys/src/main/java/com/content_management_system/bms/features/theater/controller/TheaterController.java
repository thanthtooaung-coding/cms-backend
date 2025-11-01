package com.content_management_system.bms.features.theater.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.common.dto.ErrorResponse;
import com.content_management_system.bms.features.theater.dto.CreateTheaterRequest;
import com.content_management_system.bms.features.theater.dto.TheaterResponse;
import com.content_management_system.bms.features.theater.dto.UpdateTheaterRequest;
import com.content_management_system.bms.features.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theaters")
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
    public ResponseEntity<ApiResponse<TheaterResponse>> create(@RequestBody final CreateTheaterRequest request) {
        final TheaterResponse createdTheater = theaterService.create(request);
        return ApiResponse.created(createdTheater, "Theater created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> retrieveOne(@PathVariable final Long id) {
        return ApiResponse.ok(theaterService.retrieveOne(id), "Theater retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> retrieveAll() {
        return ApiResponse.ok(theaterService.retrieveAll(), "Theaters retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> update(
            @PathVariable final Long id,
            @RequestBody final UpdateTheaterRequest request
    ) {
        return ApiResponse.ok(theaterService.update(id, request), "Theater updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final Long id) {
        theaterService.delete(id);
        return ApiResponse.noContent("Theater deleted successfully");
    }
}
