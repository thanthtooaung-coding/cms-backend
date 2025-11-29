package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.MovieStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovieStatusConverter implements AttributeConverter<MovieStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(MovieStatus status) {
        return (status != null) ? status.getId() : null;
    }

    @Override
    public MovieStatus convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? MovieStatus.fromId(dbData) : null;
    }
}
