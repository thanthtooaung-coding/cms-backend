package com.content_management_system.lms.features.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor 
@AllArgsConstructor
@SuperBuilder
public class BaseCourseCategoryRequest {
    private String name;
    private String description;
}
