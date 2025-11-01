package com.content_management_system.bms.features.movies.mapper;

import com.content_management_system.bms.common.models.Movie;
import com.content_management_system.bms.features.movies.dto.CreateMovieRequest;
import com.content_management_system.bms.features.movies.dto.MovieResponse;
import com.content_management_system.bms.features.movies.dto.UpdateMovieRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "rating", expression = "java(movie.getRating() != null ? movie.getRating().getDisplayName() : null)")
    @Mapping(target = "language", expression = "java(movie.getLanguage() != null ? movie.getLanguage().getDisplayName() : null)")
    @Mapping(target = "status", expression = "java(movie.getStatus() != null ? movie.getStatus().getDisplayName() : null)")
    MovieResponse toDto(Movie movie);

    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "status", ignore = true)
    Movie toEntity(CreateMovieRequest request);

    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateMovieFromDto(UpdateMovieRequest request, @MappingTarget Movie movie);
}
