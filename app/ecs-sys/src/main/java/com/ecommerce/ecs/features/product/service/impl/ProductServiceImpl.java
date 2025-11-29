package com.ecommerce.ecs.features.product.service.impl;

import com.ecommerce.ecs.common.exception.ResourceNotFoundException;
import com.ecommerce.ecs.common.models.Category;
import com.ecommerce.ecs.common.models.Product;
import com.ecommerce.ecs.common.models.Tenant;
import com.ecommerce.ecs.common.repository.TenantRepository;
import com.ecommerce.ecs.common.repository.jpa.CategoryJpaRepository;
import com.ecommerce.ecs.common.repository.jpa.ProductJpaRepository;
import com.ecommerce.ecs.features.product.dto.CreateProductRequest;
import com.ecommerce.ecs.features.product.dto.ProductResponse;
import com.ecommerce.ecs.features.product.dto.UpdateProductRequest;
import com.ecommerce.ecs.features.product.mapper.ProductMapper;
import com.ecommerce.ecs.features.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TenantRepository tenantRepository;
    
    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        Product product = ProductMapper.toEntity(request);
        product.setTenant(tenant);
        
        if (request.getCategoryId() != null) {
            Category category = categoryJpaRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        
        Product saved = productJpaRepository.save(product);
        return ProductMapper.toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id, Long tenantId) {
        Product product = productJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductMapper.toResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll(Long tenantId) {
        return productJpaRepository.findAllByTenantId(tenantId).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getByCategory(Long categoryId, Long tenantId) {
        return productJpaRepository.findAllByCategoryIdAndTenantId(categoryId, tenantId).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String query, Long tenantId) {
        List<Product> products = productJpaRepository.findAllByTenantId(tenantId);
        String lowerQuery = query.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                           (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerQuery)))
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request, Long tenantId) {
        Product product = productJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        ProductMapper.updateEntity(product, request);
        
        if (request.getCategoryId() != null) {
            Category category = categoryJpaRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        
        Product updated = productJpaRepository.save(product);
        return ProductMapper.toResponse(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        Product product = productJpaRepository.findById(id)
                .filter(p -> p.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productJpaRepository.delete(product);
    }
}

