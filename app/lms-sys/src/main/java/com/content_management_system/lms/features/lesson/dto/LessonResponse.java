package com.content_management_system.lms.features.lesson.dto;

import com.content_management_system.lms.shared.constants.MaterialType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private MaterialType materialType;
    private ModuleInfo module;
    
    @Data
    @Builder
    public static class ModuleInfo {
        private Long id;
        private String name;
    }
}