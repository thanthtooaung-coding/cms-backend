package com.ecommerce.ecs.common.converters;

import com.ecommerce.ecs.common.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(PaymentStatus status) {
        if (status == null) {
            return null;
        }
        return status.getId();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(Long id) {
        if (id == null) {
            return null;
        }
        return PaymentStatus.fromId(id);
    }
}

