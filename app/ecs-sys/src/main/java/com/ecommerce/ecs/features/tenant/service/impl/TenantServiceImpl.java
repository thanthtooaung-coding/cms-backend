package com.ecommerce.ecs.features.tenant.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Configuration;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.ConfigurationJpaRepository;
import com.ecommerce.ecs.features.tenant.dto.TenantRequest;
import com.ecommerce.ecs.features.tenant.dto.TenantResponse;
import com.ecommerce.ecs.features.tenant.mapper.TenantMapper;
import com.ecommerce.ecs.features.tenant.service.TenantService;
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
    @Transactional(readOnly = true)
    public TenantResponse findByName(String name) {
        Tenant tenant = tenantRepository.findAll().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with name: " + name));
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

