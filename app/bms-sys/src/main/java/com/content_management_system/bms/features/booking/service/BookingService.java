package com.content_management_system.bms.features.booking.service;

import com.content_management_system.bms.features.booking.dto.BookingDetailResponse;
import com.content_management_system.bms.features.booking.dto.BookingResponse;
import com.content_management_system.bms.features.booking.dto.CreateBookingRequest;
import com.content_management_system.bms.features.booking.dto.MyBookingsResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request, String userEmail);
    List<BookingDetailResponse> retrieveAll();
    void delete(Long id);
    MyBookingsResponse retrieveMyBookings(String userEmail);
    void cancelBooking(Long id, String username);
}
