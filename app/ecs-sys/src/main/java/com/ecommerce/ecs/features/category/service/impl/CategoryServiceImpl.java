package com.ecommerce.ecs.features.category.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Category;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.CategoryJpaRepository;
import com.ecommerce.ecs.features.category.dto.CategoryRequest;
import com.ecommerce.ecs.features.category.dto.CategoryResponse;
import com.ecommerce.ecs.features.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryJpaRepository categoryJpaRepository;
    private final TenantRepository tenantRepository;
    
    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setTenant(tenant);
        
        Category saved = categoryJpaRepository.save(category);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(Long tenantId) {
        return categoryJpaRepository.findAllByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id, Long tenantId) {
        Category category = categoryJpaRepository.findById(id)
                .filter(c -> c.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toResponse(category);
    }
    
    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request, Long tenantId) {
        Category category = categoryJpaRepository.findById(id)
                .filter(c -> c.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        
        Category updated = categoryJpaRepository.save(category);
        return toResponse(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        Category category = categoryJpaRepository.findById(id)
                .filter(c -> c.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryJpaRepository.delete(category);
    }
    
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .tenantId(category.getTenant().getId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

