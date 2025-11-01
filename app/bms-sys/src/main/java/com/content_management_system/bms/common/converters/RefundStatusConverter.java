package com.content_management_system.bms.common.converters;

import com.content_management_system.bms.common.enums.RefundStatus;
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
