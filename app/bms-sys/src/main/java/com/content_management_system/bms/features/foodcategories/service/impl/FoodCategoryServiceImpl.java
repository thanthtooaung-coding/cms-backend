package com.content_management_system.bms.features.foodcategories.service.impl;

import com.content_management_system.bms.common.models.FoodCategory;
import com.content_management_system.bms.common.repository.jpa.FoodCategoryJpaRepository;
import com.content_management_system.bms.features.foodcategories.dto.FoodCategoryResponse;
import com.content_management_system.bms.features.foodcategories.dto.CreateFoodCategoryRequest;
import com.content_management_system.bms.features.foodcategories.dto.UpdateFoodCategoryRequest;
import com.content_management_system.bms.features.foodcategories.mapper.FoodCategoryMapper;
import com.content_management_system.bms.features.foodcategories.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodCategoryServiceImpl implements FoodCategoryService {

    private final FoodCategoryJpaRepository foodCategoryJpaRepository;
    private final FoodCategoryMapper foodCategoryMapper;

    @Override
    @Transactional
    public FoodCategoryResponse create(CreateFoodCategoryRequest request) {
        final FoodCategory foodCategory = new FoodCategory(request.name(), request.description());
        final FoodCategory savedFoodCategory = this.foodCategoryJpaRepository.save(foodCategory);
        return this.foodCategoryMapper.toDto(savedFoodCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodCategoryResponse retrieveOne(Long id) {
        final FoodCategory foodCategory = this.foodCategoryJpaRepository.findById(id).orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        return this.foodCategoryMapper.toDto(foodCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodCategoryResponse> retrieveAll() {
        final List<FoodCategory> categories = foodCategoryJpaRepository.findAll();
        return this.foodCategoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional
    public FoodCategoryResponse update(Long id, UpdateFoodCategoryRequest request) {
        final FoodCategory foodCategory = this.foodCategoryJpaRepository.findById(id).orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
        foodCategory.setName(request.name());
        foodCategory.setDescription(request.description());
        final FoodCategory updatedFoodCategory = this.foodCategoryJpaRepository.save(foodCategory);
        return this.foodCategoryMapper.toDto(updatedFoodCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!foodCategoryJpaRepository.existsById(id)) {
            throw new RuntimeException("Category with id " + id + " not found");
        }
        this.foodCategoryJpaRepository.deleteById(id);
    }
}
