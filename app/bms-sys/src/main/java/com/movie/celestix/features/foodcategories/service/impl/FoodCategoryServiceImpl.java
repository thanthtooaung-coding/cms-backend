package com.movie.celestix.features.foodcategories.service.impl;

import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.FoodCategory;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.FoodCategoryJpaRepository;
import com.movie.celestix.features.foodcategories.dto.FoodCategoryResponse;
import com.movie.celestix.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.movie.celestix.features.foodcategories.dto.UpdateFoodCategoryRequest;
import com.movie.celestix.features.foodcategories.mapper.FoodCategoryMapper;
import com.movie.celestix.features.foodcategories.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodCategoryServiceImpl implements FoodCategoryService {

    private final FoodCategoryJpaRepository foodCategoryJpaRepository;
    private final FoodCategoryMapper foodCategoryMapper;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public FoodCategoryResponse create(CreateFoodCategoryRequest request, Long tenantId) {
        final FoodCategory foodCategory = new FoodCategory();
        foodCategory.setName(request.name());
        foodCategory.setDescription(request.description());
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        foodCategory.setTenant(tenant);
        
        final FoodCategory savedFoodCategory = this.foodCategoryJpaRepository.save(foodCategory);
        return this.foodCategoryMapper.toDto(savedFoodCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodCategoryResponse retrieveOne(Long id, Long tenantId) {
        final FoodCategory foodCategory = this.foodCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        // Verify tenant matches
        if (foodCategory.getTenant() == null || !foodCategory.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Category with id " + id + " not found for tenant " + tenantId);
        }
        return this.foodCategoryMapper.toDto(foodCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodCategoryResponse> retrieveAll(Long tenantId) {
        final List<FoodCategory> categories = foodCategoryJpaRepository.findAllByTenantId(tenantId);
        return this.foodCategoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional
    public FoodCategoryResponse update(Long id, UpdateFoodCategoryRequest request, Long tenantId) {
        final FoodCategory foodCategory = this.foodCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        // Verify tenant matches
        if (foodCategory.getTenant() == null || !foodCategory.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Category with id " + id + " not found for tenant " + tenantId);
        }
        foodCategory.setName(request.name());
        foodCategory.setDescription(request.description());
        final FoodCategory updatedFoodCategory = this.foodCategoryJpaRepository.save(foodCategory);
        return this.foodCategoryMapper.toDto(updatedFoodCategory);
    }

    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        final FoodCategory foodCategory = this.foodCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        // Verify tenant matches
        if (foodCategory.getTenant() == null || !foodCategory.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Category with id " + id + " not found for tenant " + tenantId);
        }
        this.foodCategoryJpaRepository.deleteById(id);
    }
}
