package com.movie.celestix.features.booking.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.common.jwt.JwtUtil;
import com.movie.celestix.features.booking.dto.BookingDetailResponse;
import com.movie.celestix.features.booking.dto.BookingResponse;
import com.movie.celestix.features.booking.dto.CreateBookingRequest;
import com.movie.celestix.features.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    private String extractEmailFromToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwt = authorizationHeader.substring(7);
            return jwtUtil.extractUsername(jwt);
        }
        throw new RuntimeException("Authorization token is required");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody CreateBookingRequest request,
            @RequestParam Long tenantId,
            HttpServletRequest httpRequest
    ) {
        String email = extractEmailFromToken(httpRequest);
        BookingResponse booking = bookingService.createBooking(request, email, tenantId);
        return ApiResponse.created(booking, "Booking created successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDetailResponse>>> retrieveAll(@RequestParam Long tenantId) {
        return ApiResponse.ok(bookingService.retrieveAll(tenantId), "Bookings retrieved successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable final Long id,
            @RequestParam Long tenantId
    ) {
        bookingService.delete(id, tenantId);
        return ApiResponse.noContent("Booking deleted successfully");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable final Long id,
            @RequestParam Long tenantId,
            HttpServletRequest request
    ) {
        String email = extractEmailFromToken(request);
        bookingService.cancelBooking(id, email, tenantId);
        return ApiResponse.ok(null, "Booking cancelled successfully");
    }
}
