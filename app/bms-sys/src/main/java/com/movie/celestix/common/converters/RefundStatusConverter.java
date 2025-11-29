package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.RefundStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RefundStatusConverter implements AttributeConverter<RefundStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(RefundStatus status) {
        return (status != null) ? status.getId() : null;
    }

    @Override
    public RefundStatus convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? RefundStatus.fromId(dbData) : null;
    }
}
