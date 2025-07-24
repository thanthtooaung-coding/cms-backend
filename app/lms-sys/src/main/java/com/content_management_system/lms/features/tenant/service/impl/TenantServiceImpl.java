package com.content_management_system.lms.features.tenant.service.impl;

import com.content_management_system.lms.features.tenant.dto.TenantRequest;
import com.content_management_system.lms.features.tenant.dto.TenantResponse;
import com.content_management_system.lms.features.tenant.mapper.TenantMapper;
import com.content_management_system.lms.features.tenant.service.TenantService;
import com.content_management_system.lms.shared.entity.Tenant;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public TenantResponse create(TenantRequest request) {
        Tenant tenant = TenantMapper.toEntity(request);
        Tenant savedTenant = tenantRepository.save(tenant);
        return TenantMapper.toResponse(savedTenant);
    }

    @Override
    public List<TenantResponse> findAll() {
        return tenantRepository.findAll().stream()
                .map(TenantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TenantResponse findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return TenantMapper.toResponse(tenant);
    }

    @Override
    public TenantResponse update(Long id, TenantRequest request) {
        Tenant existingTenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        existingTenant.setName(request.getName());
        existingTenant.setActive(request.isActive());

        Tenant updatedTenant = tenantRepository.save(existingTenant);
        return TenantMapper.toResponse(updatedTenant);
    }

    @Override
    public void deleteById(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        tenantRepository.deleteById(id);
    }
}