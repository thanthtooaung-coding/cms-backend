package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.MovieRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovieRatingConverter implements AttributeConverter<MovieRating, Long> {

    @Override
    public Long convertToDatabaseColumn(MovieRating rating) {
        return (rating != null) ? rating.getId() : null;
    }

    @Override
    public MovieRating convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? MovieRating.fromId(dbData) : null;
    }
}
