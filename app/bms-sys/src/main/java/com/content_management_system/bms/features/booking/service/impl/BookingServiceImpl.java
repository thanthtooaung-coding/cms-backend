package com.content_management_system.bms.features.booking.service.impl;

import com.content_management_system.bms.common.enums.BookingStatus;
import com.content_management_system.bms.common.enums.PaymentStatus;
import com.content_management_system.bms.common.models.*;
import com.content_management_system.bms.common.repository.jpa.*;
import com.content_management_system.bms.common.util.CreditCardValidator;
import com.content_management_system.bms.features.booking.dto.BookingDetailResponse;
import com.content_management_system.bms.features.booking.dto.BookingResponse;
import com.content_management_system.bms.features.booking.dto.CreateBookingRequest;
import com.content_management_system.bms.features.booking.dto.MyBookingsResponse;
import com.content_management_system.bms.features.booking.mapper.BookingMapper;
import com.content_management_system.bms.features.booking.service.BookingService;
import com.content_management_system.bms.features.email.dto.EmailDetails;
import com.content_management_system.bms.features.email.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingJpaRepository;
    private final ShowtimeJpaRepository showtimeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookedSeatJpaRepository bookedSeatJpaRepository;
    private final BookingMapper bookingMapper;
    private final ConfigurationJpaRepository configurationJpaRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public BookingResponse createBooking(final CreateBookingRequest request, final String userEmail) {
        if (request.cardDetails() == null) {
            throw new IllegalArgumentException("Payment details are required.");
        }
        if (!CreditCardValidator.isValid(request.cardDetails().cardNumber())) {
            throw new IllegalArgumentException("Invalid credit card number provided.");
        }

        final User user = userJpaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        final Configuration config = configurationJpaRepository.findByCode("MAX_BOOKINGS_PER_USER")
                .orElse(new Configuration("MAX_BOOKINGS_PER_USER", "10"));
        final int maxBookings = Integer.parseInt(config.getValue());

        if (request.seatNumbers().size() >= maxBookings) {
            throw new IllegalStateException("You have reached the maximum booking limit of " + maxBookings + ".");
        }

        final Showtime showtime = showtimeJpaRepository.findById(request.showtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        final List<String> alreadyBookedSeats = bookedSeatJpaRepository.findByShowtimeId(showtime.getId())
                .stream()
                .map(BookedSeat::getSeatNumber)
                .toList();

        for (String seatNumber : request.seatNumbers()) {
            if (alreadyBookedSeats.contains(seatNumber)) {
                throw new IllegalStateException("Seat " + seatNumber + " is already booked.");
            }
        }

        final Booking booking = new Booking();
        booking.setBookingId(UUID.randomUUID().toString());
        booking.setUser(user);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
//        booking.setPaymentStatus(PaymentStatus.SUCCESS);

        final Set<BookedSeat> bookedSeats = new HashSet<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (String seatNumber : request.seatNumbers()) {
            final BookedSeat bookedSeat = new BookedSeat();
            BigDecimal price = calculatePrice(showtime.getTheater(), seatNumber);
            totalPrice = totalPrice.add(price);
            bookedSeat.setBooking(booking);
            bookedSeat.setShowtime(showtime);
            bookedSeat.setSeatNumber(seatNumber);
            bookedSeat.setPrice(price);
            bookedSeats.add(bookedSeat);
        }

        booking.setBookedSeats(bookedSeats);
        booking.setTotalPrice(totalPrice);

        String header = "CELESTIX-"+booking.getUser().getName()+request.cardDetails().cardNumber().substring(request.cardDetails().cardNumber().length() - 4);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("PAYMENT-INITIATOR", header.toString());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", totalPrice);
        body.put("currency", "USD");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);
        
        RestTemplate restTemplate = new RestTemplate();
        String paymentUrl = "https://aungkhaingkhant.online/test-bank/api/make-payment";
        Map<String, Object> paymentResponse = restTemplate.postForObject(paymentUrl, entity, Map.class);

        if (!paymentResponse.get("status").equals("SUCCESS")) {
            booking.setPaymentStatus(PaymentStatus.FAIL);
        }else{
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
        }
        showtime.setSeatsAvailable(showtime.getSeatsAvailable() - request.seatNumbers().size());

        final Booking savedBooking = bookingJpaRepository.save(booking);
        showtimeJpaRepository.save(showtime);

        if (savedBooking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            final String seats = savedBooking.getBookedSeats().stream()
                    .map(BookedSeat::getSeatNumber)
                    .sorted()
                    .collect(Collectors.joining(", "));

            final EmailDetails emailDetails = new EmailDetails(
                    savedBooking.getUser().getName(),
                    savedBooking.getUser().getEmail(),
                    savedBooking.getBookingId(),
                    showtime.getMovie().getTitle(),
                    showtime.getTheater().getName(),
                    showtime.getShowtimeDate().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")),
                    showtime.getShowtimeTime().format(DateTimeFormatter.ofPattern("hh:mm a")),
                    seats,
                    savedBooking.getTotalPrice()
            );
            emailService.sendBookingConfirmationEmail(emailDetails);
        }

        return bookingMapper.toDto(booking);
    }

    /**
     * Calculates the price of a seat based on its row and the theater's pricing configuration.
     * This logic mirrors the frontend's seat type determination.
     *
     * @param theater The theater where the showtime is happening.
     * @param seatNumber The seat identifier, e.g., "A5", "C12".
     * @return The price of the seat as a BigDecimal.
     */
    private BigDecimal calculatePrice(final Theater theater, final String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty() || !Character.isLetter(seatNumber.charAt(0))) {
            throw new IllegalArgumentException("Invalid seat number format: " + seatNumber);
        }

        final char rowChar = Character.toUpperCase(seatNumber.charAt(0));
        final int rowIndex = rowChar - 'A';

        final int premiumRows = theater.getPremiumSeat().getTotalRows();
        final int regularRows = theater.getRegularSeat().getTotalRows();
        final int economyRows = theater.getEconomySeat().getTotalRows();

        if (rowIndex < premiumRows) {
            return theater.getPremiumSeat().getTotalPrice();
        } else if (rowIndex < premiumRows + regularRows) {
            return theater.getRegularSeat().getTotalPrice();
        } else if (rowIndex < premiumRows + regularRows + economyRows) {
            return theater.getEconomySeat().getTotalPrice();
        } else {
            return theater.getBasicSeat().getTotalPrice();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDetailResponse> retrieveAll() {
        List<Booking> bookings = bookingJpaRepository.findAll();
        return bookingMapper.toDetailDtoList(bookings);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        final Booking booking = bookingJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking with id " + id + " not found"));

        if (!booking.getBookedSeats().isEmpty()) {
            final Showtime showtime = booking.getBookedSeats().iterator().next().getShowtime();
            showtime.setSeatsAvailable(showtime.getSeatsAvailable() + booking.getBookedSeats().size());
            showtimeJpaRepository.save(showtime);
        }

        bookingJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MyBookingsResponse retrieveMyBookings(final String userEmail) {
        final List<Booking> bookings = bookingJpaRepository.findByUserEmail(userEmail);
        final List<BookingDetailResponse> allBookings = bookingMapper.toDetailDtoList(bookings);

        final LocalDate now = LocalDate.now();
        final LocalTime nowTime = LocalTime.now();

        final List<BookingDetailResponse> upcoming = allBookings.stream()
                .filter(b -> b.status().equals(BookingStatus.CONFIRMED.getDisplayName()))
                .filter(b -> b.showtimeDate().isAfter(now) || (b.showtimeDate().isEqual(now) && b.showtimeTime().isAfter(nowTime)))
                .collect(Collectors.toList());

        final List<BookingDetailResponse> completed = allBookings.stream()
                .filter(b -> b.status().equals(BookingStatus.CANCELLED.getDisplayName())
                                || b.showtimeDate().isBefore(now) || (b.showtimeDate().isEqual(now) && !b.showtimeTime().isAfter(nowTime)))
                .collect(Collectors.toList());

        return new MyBookingsResponse(upcoming, completed);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String userEmail) {
        final Booking booking = bookingJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking with id " + id + " not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("You are not authorized to cancel this booking.");
        }

        final Configuration config = configurationJpaRepository.findByCode("CANCELLATION_MINUTES")
                .orElse(new Configuration("CANCELLATION_MINUTES", "15"));
        final int cancellationMinutes = Integer.parseInt(config.getValue());

        final Showtime showtime = booking.getBookedSeats().iterator().next().getShowtime();
        final LocalDateTime showtimeDateTime = LocalDateTime.of(showtime.getShowtimeDate(), showtime.getShowtimeTime());
        final LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(showtimeDateTime.minusMinutes(cancellationMinutes))) {
            throw new IllegalStateException("You can only cancel a booking up to " + cancellationMinutes + " minutes before the showtime.");
        }

        if (!booking.getBookedSeats().isEmpty()) {
            showtime.setSeatsAvailable(showtime.getSeatsAvailable() + booking.getBookedSeats().size());
            booking.getBookedSeats().clear();
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
//        showtime.setSeatsAvailable(showtime.getSeatsAvailable() + booking.getBookedSeats().size());

        bookingJpaRepository.save(booking);
        showtimeJpaRepository.save(showtime);
    }
}