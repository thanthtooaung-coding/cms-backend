package com.content_management_system.lms.features.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.content_management_system.lms.features.category.dto.CourseCategoryResponse;
import com.content_management_system.lms.features.category.dto.CreateCourseCategoryRequest;
import com.content_management_system.lms.features.category.dto.UpdateCourseCategoryRequest;
import com.content_management_system.lms.features.category.service.CourseCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final  CourseCategoryService ccService;

    @PostMapping
    public ResponseEntity<CourseCategoryResponse> createCategory(@RequestBody CreateCourseCategoryRequest request) {
        CourseCategoryResponse response = ccService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseCategoryResponse>> getAllTenants() {
        List<CourseCategoryResponse> responses = ccService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseCategoryResponse> getTenantById(@PathVariable Long id) {
    	CourseCategoryResponse response = ccService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseCategoryResponse> updateTenant(@PathVariable Long id, @RequestBody UpdateCourseCategoryRequest request) {
    	CourseCategoryResponse response = ccService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
    	ccService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}