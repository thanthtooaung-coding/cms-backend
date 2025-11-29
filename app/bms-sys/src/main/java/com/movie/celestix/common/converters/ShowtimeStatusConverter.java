package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.ShowtimeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShowtimeStatusConverter implements AttributeConverter<ShowtimeStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ShowtimeStatus status) {
        return (status != null) ? status.getId() : null;
    }

    @Override
    public ShowtimeStatus convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? ShowtimeStatus.fromId(dbData) : null;
    }
}
