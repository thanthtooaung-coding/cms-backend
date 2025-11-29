package com.ecommerce.ecs.features.product.controller;

import com.ecommerce.ecs.common.dto.ApiResponse;
import com.ecommerce.ecs.features.product.dto.CreateProductRequest;
import com.ecommerce.ecs.features.product.dto.ProductResponse;
import com.ecommerce.ecs.features.product.dto.UpdateProductRequest;
import com.ecommerce.ecs.features.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestBody CreateProductRequest request,
            @RequestParam Long tenantId) {
        ProductResponse response = productService.create(request, tenantId);
        return ApiResponse.created(response, "Product created successfully");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        ProductResponse response = productService.getById(id, tenantId);
        return ApiResponse.ok(response, "Product retrieved successfully");
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        List<ProductResponse> products;
        if (categoryId != null) {
            products = productService.getByCategory(categoryId, tenantId);
        } else if (search != null && !search.isEmpty()) {
            products = productService.search(search, tenantId);
        } else {
            products = productService.getAll(tenantId);
        }
        return ApiResponse.ok(products, "Products retrieved successfully");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request,
            @RequestParam Long tenantId) {
        ProductResponse response = productService.update(id, request, tenantId);
        return ApiResponse.ok(response, "Product updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        productService.delete(id, tenantId);
        return ApiResponse.noContent("Product deleted successfully");
    }
}

