package com.content_management_system.lms.features.tenant.service;

import com.content_management_system.lms.features.tenant.dto.TenantRequest;
import com.content_management_system.lms.features.tenant.dto.TenantResponse;

import java.util.List;

public interface TenantService {

    TenantResponse create(TenantRequest request);

    List<TenantResponse> findAll();

    TenantResponse findById(Long id);

    TenantResponse update(Long id, TenantRequest request);

    void deleteById(Long id);
}