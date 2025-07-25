package com.content_management_system.lms.features.module.mapper;

import com.content_management_system.lms.features.module.dto.ModuleResponse;
import com.content_management_system.lms.shared.entity.Module;

public class ModuleMapper {

    public static ModuleResponse toResponse(Module module) {
        ModuleResponse.CourseInfo courseInfo = null;
        if (module.getCourse() != null) {
        	courseInfo = ModuleResponse.CourseInfo.builder()
                    .id(module.getCourse().getId())
                    .name(module.getCourse().getTitle())
                    .build();
        }

        return ModuleResponse.builder()
                .name(module.getName())
                .description(module.getDescription())
                .course(courseInfo)
                .build();
    }
}