package com.movie.celestix.common.models;

import com.movie.celestix.common.converters.BookingStatusConverter;
import com.movie.celestix.common.converters.PaymentStatusConverter;
import com.movie.celestix.common.enums.BookingStatus;
import com.movie.celestix.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "UPDATE bookings SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Booking extends MasterData {
    private String bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = BookingStatusConverter.class)
    private BookingStatus bookingStatus;

    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookedSeat> bookedSeats = new HashSet<>();

    private BigDecimal totalPrice;
}
