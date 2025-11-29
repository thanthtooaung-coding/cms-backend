package com.movie.celestix.features.booking.service;

import com.movie.celestix.features.booking.dto.BookingDetailResponse;
import com.movie.celestix.features.booking.dto.BookingResponse;
import com.movie.celestix.features.booking.dto.CreateBookingRequest;
import com.movie.celestix.features.booking.dto.MyBookingsResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request, String userEmail, Long tenantId);
    List<BookingDetailResponse> retrieveAll(Long tenantId);
    void delete(Long id, Long tenantId);
    MyBookingsResponse retrieveMyBookings(String userEmail, Long tenantId);
    void cancelBooking(Long id, String username, Long tenantId);
}
