package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.BookingRefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRefundRecordJpaRepository extends JpaRepository<BookingRefundRecord, Long> {
    Optional<BookingRefundRecord> findByBookingId(Long bookingId);
}
