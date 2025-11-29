package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(PaymentStatus status) {
        return (status != null) ? status.getId() : null;
    }

    @Override
    public PaymentStatus convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? PaymentStatus.fromId(dbData) : null;
    }
}
