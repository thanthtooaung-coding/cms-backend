package com.ecommerce.ecs.features.tenant.service;

import com.ecommerce.ecs.features.tenant.dto.TenantRequest;
import com.ecommerce.ecs.features.tenant.dto.TenantResponse;

import java.util.List;

public interface TenantService {
    TenantResponse create(TenantRequest request);
    List<TenantResponse> findAll();
    TenantResponse findById(Long id);
    TenantResponse findByName(String name);
    TenantResponse update(Long id, TenantRequest request);
    void deleteById(Long id);
}

