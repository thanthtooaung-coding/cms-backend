package com.content_management_system.lms.features.lesson.dto;

import com.content_management_system.lms.shared.constants.MaterialType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor 
@AllArgsConstructor
@SuperBuilder
public abstract class BaseLessonRequest {
    private String title;
    private String content;
    private MaterialType materialType;
}
