package com.content_management_system.bms.features.admins.mapper;

import com.content_management_system.bms.common.models.User;
import com.content_management_system.bms.features.admins.dto.AdminResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminResponse toDto(User user);
    List<AdminResponse> toDtoList(List<User> users);
}
