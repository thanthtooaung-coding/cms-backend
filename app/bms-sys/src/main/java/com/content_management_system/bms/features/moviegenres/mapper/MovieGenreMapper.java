package com.content_management_system.bms.features.moviegenres.mapper;

import com.content_management_system.bms.common.models.MovieGenre;
import com.content_management_system.bms.features.moviegenres.dto.MovieGenreResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieGenreMapper {
    MovieGenreResponse toDto(MovieGenre movieGenre);
    List<MovieGenreResponse> toDtoList(List<MovieGenre> genres);
}
