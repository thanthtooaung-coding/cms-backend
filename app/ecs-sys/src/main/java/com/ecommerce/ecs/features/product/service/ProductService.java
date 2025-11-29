package com.ecommerce.ecs.features.product.service;

import com.ecommerce.ecs.features.product.dto.CreateProductRequest;
import com.ecommerce.ecs.features.product.dto.ProductResponse;
import com.ecommerce.ecs.features.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest request, Long tenantId);
    ProductResponse getById(Long id, Long tenantId);
    List<ProductResponse> getAll(Long tenantId);
    List<ProductResponse> getByCategory(Long categoryId, Long tenantId);
    List<ProductResponse> search(String query, Long tenantId);
    ProductResponse update(Long id, UpdateProductRequest request, Long tenantId);
    void delete(Long id, Long tenantId);
}

