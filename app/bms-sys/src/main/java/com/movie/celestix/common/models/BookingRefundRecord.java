package com.movie.celestix.common.models;

import com.movie.celestix.common.converters.RefundStatusConverter;
import com.movie.celestix.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking_refund_records")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BookingRefundRecord extends MasterData {

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Convert(converter = RefundStatusConverter.class)
    private RefundStatus status;
}
