package com.content_management_system.lms.features.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.content_management_system.lms.features.category.dto.CourseCategoryResponse;
import com.content_management_system.lms.features.category.dto.CreateCourseCategoryRequest;
import com.content_management_system.lms.features.category.dto.UpdateCourseCategoryRequest;
import com.content_management_system.lms.features.category.mapper.CourseCategoryMapper;
import com.content_management_system.lms.features.category.service.CourseCategoryService;
import com.content_management_system.lms.shared.entity.CourseCategory;
import com.content_management_system.lms.shared.entity.Tenant;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.CategoryRepository;
import com.content_management_system.lms.shared.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {

    private final CategoryRepository courseCategoryRepo;
    private final TenantRepository tenantRepo;
    @Override
    public CourseCategoryResponse create(CreateCourseCategoryRequest request) {
        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setName(request.getName());
        courseCategory.setDescription(request.getDescription());
        Tenant tenant = tenantRepo.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with: " + request.getTenantId()));
        courseCategory.setTenant(tenant);
        CourseCategory savedCC = courseCategoryRepo.save(courseCategory);
        return CourseCategoryMapper.toResponse(savedCC);
    }

    @Override
    public List<CourseCategoryResponse> findAll() {
        return courseCategoryRepo.findAll().stream()
                .map(CourseCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseCategoryResponse findById(Long id) {
    	CourseCategory courseCategory = courseCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Category not found with id: " + id));
        return CourseCategoryMapper.toResponse(courseCategory);
    }

    @Override
    public CourseCategoryResponse update(Long id, UpdateCourseCategoryRequest request) {
    	CourseCategory existingCC = courseCategoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Category not found with id: " + id));

        existingCC.setName(request.getName());
        existingCC.setDescription(request.getDescription());

        CourseCategory updatedCC = courseCategoryRepo.save(existingCC);
        return CourseCategoryMapper.toResponse(updatedCC);
    }

    @Override
    public void deleteById(Long id) {
        if (!courseCategoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        courseCategoryRepo.deleteById(id);
    }
}