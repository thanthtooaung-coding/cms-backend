package com.ecommerce.ecs.common.converters;

import com.ecommerce.ecs.common.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return status.getId();
    }

    @Override
    public OrderStatus convertToEntityAttribute(Long id) {
        if (id == null) {
            return null;
        }
        return OrderStatus.fromId(id);
    }
}

