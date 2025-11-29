package com.movie.celestix.features.booking.mapper;

import com.movie.celestix.common.models.BookedSeat;
import com.movie.celestix.common.models.Booking;
import com.movie.celestix.common.models.Showtime;
import com.movie.celestix.common.repository.jpa.BookingRefundRecordJpaRepository;
import com.movie.celestix.features.booking.dto.BookedSeatDto;
import com.movie.celestix.features.booking.dto.BookingDetailResponse;
import com.movie.celestix.features.booking.dto.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Autowired
    protected BookingRefundRecordJpaRepository refundRecordJpaRepository;

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "totalPrice", target = "totalPrice")
    public abstract BookingResponse toDto(Booking booking);

    public abstract BookedSeatDto toDto(BookedSeat bookedSeat);

    @Mapping(target = "customerName", source = "user.name")
    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "status", expression = "java(booking.getBookingStatus().getDisplayName())")
    @Mapping(target = "paymentStatus", expression = "java(booking.getPaymentStatus().getDisplayName())")
    @Mapping(target = "seats", source = "bookedSeats", qualifiedByName = "bookedSeatsToString")
    @Mapping(target = "movieTitle", source = "booking", qualifiedByName = "movieTitleFromBooking")
    @Mapping(target = "theaterName", source = "booking", qualifiedByName = "theaterNameFromBooking")
    @Mapping(target = "bookingDate", expression = "java(booking.getCreatedAt() != null ? booking.getCreatedAt().toLocalDate() : null)")
    @Mapping(target = "bookingTime", expression = "java(booking.getCreatedAt() != null ? booking.getCreatedAt().atZoneSameInstant(java.time.ZoneId.of(\"Asia/Yangon\")).format(java.time.format.DateTimeFormatter.ofPattern(\"HH:mm\")) : null)")
    @Mapping(target = "showtimeDate", source = "booking", qualifiedByName = "showtimeDateFromBooking")
    @Mapping(target = "showtimeTime", source = "booking", qualifiedByName = "showtimeTimeFromBooking")
    @Mapping(target = "totalAmount", source = "totalPrice")
    @Mapping(target = "isAlreadyRequestRefund", expression = "java(refundRecordJpaRepository.findByBookingId(booking.getId()).isPresent())")
    public abstract BookingDetailResponse toDetailDto(Booking booking);

    public abstract List<BookingDetailResponse> toDetailDtoList(List<Booking> bookings);

    @Named("bookedSeatsToString")
    public String bookedSeatsToString(Set<BookedSeat> bookedSeats) {
        if (bookedSeats == null || bookedSeats.isEmpty()) {
            return "";
        }
        return bookedSeats.stream()
                .map(BookedSeat::getSeatNumber)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @Named("getShowtimeFromBooking")
    public Showtime getShowtimeFromBooking(Booking booking) {
        if (booking == null || booking.getBookedSeats() == null || booking.getBookedSeats().isEmpty()) {
            return null;
        }
        return booking.getBookedSeats().iterator().next().getShowtime();
    }

    @Named("movieTitleFromBooking")
    public String movieTitleFromBooking(Booking booking) {
        Showtime showtime = getShowtimeFromBooking(booking);
        return (showtime != null && showtime.getMovie() != null) ? showtime.getMovie().getTitle() : null;
    }

    @Named("showtimeDateFromBooking")
    public java.time.LocalDate showtimeDateFromBooking(Booking booking) {
        Showtime showtime = getShowtimeFromBooking(booking);
        return (showtime != null) ? showtime.getShowtimeDate() : null;
    }

    @Named("showtimeTimeFromBooking")
    public java.time.LocalTime showtimeTimeFromBooking(Booking booking) {
        Showtime showtime = getShowtimeFromBooking(booking);
        return (showtime != null) ? showtime.getShowtimeTime() : null;
    }

    @Named("theaterNameFromBooking")
    public String theaterNameFromBooking(Booking booking) {
        Showtime showtime = getShowtimeFromBooking(booking);
        return (showtime != null && showtime.getTheater() != null) ? showtime.getTheater().getName() : null;
    }
}
