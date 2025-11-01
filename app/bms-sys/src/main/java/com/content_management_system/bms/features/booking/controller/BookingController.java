package com.content_management_system.bms.features.booking.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.booking.dto.BookingDetailResponse;
import com.content_management_system.bms.features.booking.dto.BookingResponse;
import com.content_management_system.bms.features.booking.dto.CreateBookingRequest;
import com.content_management_system.bms.features.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        BookingResponse booking = bookingService.createBooking(request, userDetails.getUsername());
        return ApiResponse.created(booking, "Booking created successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDetailResponse>>> retrieveAll() {
        return ApiResponse.ok(bookingService.retrieveAll(), "Bookings retrieved successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final Long id) {
        bookingService.delete(id);
        return ApiResponse.noContent("Booking deleted successfully");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable final Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        bookingService.cancelBooking(id, userDetails.getUsername());
        return ApiResponse.ok(null, "Booking cancelled successfully");
    }
}
