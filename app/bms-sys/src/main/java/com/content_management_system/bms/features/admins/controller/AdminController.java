package com.content_management_system.bms.features.admins.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.admins.dto.AdminResponse;
import com.content_management_system.bms.features.admins.dto.CreateAdminRequest;
import com.content_management_system.bms.features.admins.dto.UpdateAdminRequest;
import com.content_management_system.bms.features.admins.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(@RequestBody CreateAdminRequest createAdminRequest) {
        AdminResponse adminResponse = adminService.createAdmin(createAdminRequest);
        return ApiResponse.created(adminResponse, "Admin created successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminResponse>>> getAllAdmins() {
        List<AdminResponse> adminResponses = adminService.getAllAdmins();
        return ApiResponse.ok(adminResponses, "Admins retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdminById(@PathVariable Long id) {
        AdminResponse adminResponse = adminService.getAdminById(id);
        return ApiResponse.ok(adminResponse, "Admin retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(@PathVariable Long id, @RequestBody UpdateAdminRequest updateAdminRequest) {
        AdminResponse adminResponse = adminService.updateAdmin(id, updateAdminRequest);
        return ApiResponse.ok(adminResponse, "Admin updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ApiResponse.noContent("Admin deleted successfully");
    }
}
