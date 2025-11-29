package com.movie.celestix.features.admins.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.admins.dto.AdminResponse;
import com.movie.celestix.features.admins.dto.CreateAdminRequest;
import com.movie.celestix.features.admins.dto.UpdateAdminRequest;
import com.movie.celestix.features.admins.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(@RequestBody CreateAdminRequest createAdminRequest) {
        AdminResponse adminResponse = adminService.createAdmin(createAdminRequest);
        return ApiResponse.created(adminResponse, "Admin created successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminResponse>>> getAllAdmins(@RequestParam Long tenantId) {
        List<AdminResponse> adminResponses = adminService.getAllAdmins(tenantId);
        return ApiResponse.ok(adminResponses, "Admins retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdminById(@PathVariable Long id, @RequestParam Long tenantId) {
        AdminResponse adminResponse = adminService.getAdminById(id, tenantId);
        return ApiResponse.ok(adminResponse, "Admin retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(@PathVariable Long id, @RequestBody UpdateAdminRequest updateAdminRequest, @RequestParam Long tenantId) {
        AdminResponse adminResponse = adminService.updateAdmin(id, updateAdminRequest, tenantId);
        return ApiResponse.ok(adminResponse, "Admin updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id, @RequestParam Long tenantId) {
        adminService.deleteAdmin(id, tenantId);
        return ApiResponse.noContent("Admin deleted successfully");
    }
}
