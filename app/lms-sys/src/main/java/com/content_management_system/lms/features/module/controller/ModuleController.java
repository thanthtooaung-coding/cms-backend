package com.content_management_system.lms.features.module.controller;

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

import com.content_management_system.lms.features.module.dto.CreateModuleRequest;
import com.content_management_system.lms.features.module.dto.ModuleResponse;
import com.content_management_system.lms.features.module.dto.UpdateModuleRequest;
import com.content_management_system.lms.features.module.service.ModuleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lms/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponse> createModule(@RequestBody CreateModuleRequest request) {
        ModuleResponse response = moduleService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> getAllModules() {
        List<ModuleResponse> responses = moduleService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getModuleById(@PathVariable Long id) {
        ModuleResponse response = moduleService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponse> updateModule(@PathVariable Long id, @RequestBody UpdateModuleRequest request) {
        ModuleResponse response = moduleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}