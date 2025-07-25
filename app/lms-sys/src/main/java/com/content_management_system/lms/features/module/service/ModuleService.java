package com.content_management_system.lms.features.module.service;

import java.util.List;

import com.content_management_system.lms.features.module.dto.CreateModuleRequest;
import com.content_management_system.lms.features.module.dto.ModuleResponse;
import com.content_management_system.lms.features.module.dto.UpdateModuleRequest;

public interface ModuleService {

    ModuleResponse create(CreateModuleRequest request);

    List<ModuleResponse> findAll();

    ModuleResponse findById(Long id);

    ModuleResponse update(Long id, UpdateModuleRequest request);
    
    void deleteById(Long id);
}