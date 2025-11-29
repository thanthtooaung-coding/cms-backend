package com.ecommerce.ecs.features.category.service;

import com.ecommerce.ecs.features.category.dto.CategoryRequest;
import com.ecommerce.ecs.features.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request, Long tenantId);
    List<CategoryResponse> getAll(Long tenantId);
    CategoryResponse getById(Long id, Long tenantId);
    CategoryResponse update(Long id, CategoryRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}

