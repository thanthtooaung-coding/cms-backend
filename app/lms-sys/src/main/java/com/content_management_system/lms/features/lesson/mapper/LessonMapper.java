package com.content_management_system.lms.features.lesson.mapper;

import com.content_management_system.lms.features.lesson.dto.LessonResponse;
import com.content_management_system.lms.shared.entity.Lesson;

public class LessonMapper {

    public static LessonResponse toResponse(Lesson lesson) {
    	LessonResponse.ModuleInfo moduleInfo = null;
        if (lesson.getModule() != null) {
        	moduleInfo = LessonResponse.ModuleInfo.builder()
                    .id(lesson.getModule().getId())
                    .name(lesson.getModule().getName())
                    .build();
        }

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .materialType(lesson.getMaterialType())
                .module(moduleInfo)
                .build();
    }
}