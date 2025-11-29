package com.movie.celestix.common.converters;

import com.movie.celestix.common.enums.MovieLanguage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovieLanguageConverter implements AttributeConverter<MovieLanguage, Long> {

    @Override
    public Long convertToDatabaseColumn(MovieLanguage language) {
        return (language != null) ? language.getId() : null;
    }

    @Override
    public MovieLanguage convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? MovieLanguage.fromId(dbData) : null;
    }
}
