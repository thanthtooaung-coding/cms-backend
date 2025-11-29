package com.movie.celestix.features.admins.mapper;

import com.movie.celestix.common.models.User;
import com.movie.celestix.features.admins.dto.AdminResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminResponse toDto(User user);
    List<AdminResponse> toDtoList(List<User> users);
}
