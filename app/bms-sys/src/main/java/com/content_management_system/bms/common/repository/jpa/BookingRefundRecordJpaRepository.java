package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.BookingRefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRefundRecordJpaRepository extends JpaRepository<BookingRefundRecord, Long> {
    Optional<BookingRefundRecord> findByBookingId(Long bookingId);
}
