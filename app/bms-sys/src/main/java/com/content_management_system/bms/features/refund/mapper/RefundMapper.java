package com.content_management_system.bms.features.refund.mapper;

import com.content_management_system.bms.common.models.BookingRefundRecord;
import com.content_management_system.bms.features.refund.dto.RefundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "booking.user.name", target = "customerName")
    @Mapping(source = "booking.user.email", target = "customerEmail")
    @Mapping(source = "approvedBy.name", target = "approvedAdminName")
    @Mapping(source = "approvedBy.email", target = "approvedAdminEmail")
    @Mapping(source = "status", target = "status")
    RefundResponse toDto(BookingRefundRecord bookingRefundRecord);

    List<RefundResponse> toDtoList(List<BookingRefundRecord> bookingRefundRecords);
}
