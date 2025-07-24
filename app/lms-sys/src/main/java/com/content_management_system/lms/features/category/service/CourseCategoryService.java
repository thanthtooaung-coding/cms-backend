package com.content_management_system.lms.features.category.service;

import java.util.List;

import com.content_management_system.lms.features.category.dto.CourseCategoryResponse;
import com.content_management_system.lms.features.category.dto.CreateCourseCategoryRequest;
import com.content_management_system.lms.features.category.dto.UpdateCourseCategoryRequest;

public interface CourseCategoryService {

    CourseCategoryResponse create(CreateCourseCategoryRequest request);

    List<CourseCategoryResponse> findAll();

    CourseCategoryResponse findById(Long id);

    CourseCategoryResponse update(Long id, UpdateCourseCategoryRequest request);

    void deleteById(Long id);
}