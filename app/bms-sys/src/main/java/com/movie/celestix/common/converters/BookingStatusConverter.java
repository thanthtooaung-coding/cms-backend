package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.BookingStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BookingStatusConverter implements AttributeConverter<BookingStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(BookingStatus status) { return (status != null) ? status.getId() : null; }

    @Override
    public BookingStatus convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? BookingStatus.fromId(dbData) : null;
    }
}
