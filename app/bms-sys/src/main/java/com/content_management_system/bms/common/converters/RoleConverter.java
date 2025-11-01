package com.content_management_system.bms.common.converters;

import com.content_management_system.bms.common.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Long> {

    @Override
    public Long convertToDatabaseColumn(Role role) {
        return (role != null) ? role.getId() : null;
    }

    @Override
    public Role convertToEntityAttribute(Long dbData) {
        return (dbData != null) ? Role.fromId(dbData) : null;
    }
}
