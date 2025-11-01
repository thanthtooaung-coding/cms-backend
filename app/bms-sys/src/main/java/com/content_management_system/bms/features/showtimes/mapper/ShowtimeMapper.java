package com.content_management_system.bms.features.showtimes.mapper;

import com.content_management_system.bms.common.models.Showtime;
import com.content_management_system.bms.features.showtimes.dto.ShowtimeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(source = "movie.id", target = "movie.id")
    @Mapping(source = "movie.title", target = "movie.title")
    @Mapping(source = "theater.id", target = "theater.id")
    @Mapping(source = "theater.name", target = "theater.name")
    @Mapping(target = "status", expression = "java(showtime.getStatus().getDisplayName())")
    @Mapping(target = "bookedSeats", expression = "java(showtime.getBookedSeats().stream().map(com.content_management_system.bms.common.models.BookedSeat::getSeatNumber).collect(java.util.stream.Collectors.toList()))")
    ShowtimeResponse toDto(Showtime showtime);
}
