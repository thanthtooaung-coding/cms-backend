package com.content_management_system.lms.features.category.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class UpdateCourseCategoryRequest extends BaseCourseCategoryRequest {
}
