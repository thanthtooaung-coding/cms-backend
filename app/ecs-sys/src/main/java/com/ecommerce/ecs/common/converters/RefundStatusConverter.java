package com.ecommerce.ecs.common.converters;

import com.ecommerce.ecs.common.enums.RefundStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RefundStatusConverter implements AttributeConverter<RefundStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(RefundStatus status) {
        if (status == null) {
            return null;
        }
        return status.getId();
    }

    @Override
    public RefundStatus convertToEntityAttribute(Long id) {
        if (id == null) {
            return null;
        }
        return RefundStatus.fromId(id);
    }
}

