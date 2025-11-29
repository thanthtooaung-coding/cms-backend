package com.movie.celestix.features.moviegenres.mapper;

import com.movie.celestix.common.models.MovieGenre;
import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieGenreMapper {
    MovieGenreResponse toDto(MovieGenre movieGenre);
    List<MovieGenreResponse> toDtoList(List<MovieGenre> genres);
}
