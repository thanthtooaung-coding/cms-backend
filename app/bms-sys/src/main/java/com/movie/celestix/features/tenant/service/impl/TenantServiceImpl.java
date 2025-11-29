package com.movie.celestix.features.tenant.service.impl;

import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.Configuration;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.ConfigurationJpaRepository;
import com.movie.celestix.features.tenant.dto.TenantRequest;
import com.movie.celestix.features.tenant.dto.TenantResponse;
import com.movie.celestix.features.tenant.mapper.TenantMapper;
import com.movie.celestix.features.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final ConfigurationJpaRepository configurationJpaRepository;

    @Override
    @Transactional
    public TenantResponse create(TenantRequest request) {
        Tenant tenant = TenantMapper.toEntity(request);
        Tenant savedTenant = tenantRepository.save(tenant);
        
        // Create default configurations for the new tenant
        createDefaultConfigurationsForTenant(savedTenant);
        
        return TenantMapper.toResponse(savedTenant);
    }
    
    private void createDefaultConfigurationsForTenant(Tenant tenant) {
        // Get all default configurations (where tenant is null)
        List<Configuration> defaultConfigurations = configurationJpaRepository.findAllByTenantIsNull();
        
        // Create new configurations for this tenant based on defaults
        for (Configuration defaultConfig : defaultConfigurations) {
            Configuration tenantConfig = new Configuration();
            tenantConfig.setCode(defaultConfig.getCode());
            tenantConfig.setValue(defaultConfig.getValue());
            tenantConfig.setTenant(tenant);
            configurationJpaRepository.save(tenantConfig);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> findAll() {
        return tenantRepository.findAll().stream()
                .map(TenantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return TenantMapper.toResponse(tenant);
    }

    @Override
    @Transactional
    public TenantResponse update(Long id, TenantRequest request) {
        Tenant existingTenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        existingTenant.setName(request.getName());
        existingTenant.setActive(request.isActive());

        Tenant updatedTenant = tenantRepository.save(existingTenant);
        return TenantMapper.toResponse(updatedTenant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        tenantRepository.deleteById(id);
    }
}

