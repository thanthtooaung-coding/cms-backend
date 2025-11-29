package com.ecommerce.ecs.common.converters;

import com.ecommerce.ecs.common.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Long> {

    @Override
    public Long convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.getId();
    }

    @Override
    public Role convertToEntityAttribute(Long id) {
        if (id == null) {
            return null;
        }
        return Role.fromId(id);
    }
}

