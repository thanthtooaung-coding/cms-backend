package com.content_management_system.lms.features.module.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.content_management_system.lms.features.module.dto.CreateModuleRequest;
import com.content_management_system.lms.features.module.dto.ModuleResponse;
import com.content_management_system.lms.features.module.dto.UpdateModuleRequest;
import com.content_management_system.lms.features.module.mapper.ModuleMapper;
import com.content_management_system.lms.features.module.service.ModuleService;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public ModuleResponse create(CreateModuleRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        Module module = new Module();
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setCourse(course);
        Module savedModule = moduleRepository.save(module);
        return ModuleMapper.toResponse(savedModule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> findAll() {
        return moduleRepository.findAll().stream()
                .map(ModuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleResponse findById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + id));
        return ModuleMapper.toResponse(module);
    }

    @Override
    @Transactional
    public ModuleResponse update(Long id, UpdateModuleRequest request) {
    	Module existingmodule = moduleRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + id));

    	existingmodule.setName(request.getName());
    	existingmodule.setDescription(request.getDescription());

        Module updatedModule = moduleRepository.save(existingmodule);
        return ModuleMapper.toResponse(updatedModule);
    }
    
    @Override
    public void deleteById(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Module not found with id: " + id);
        }
        moduleRepository.deleteById(id);
    }
}