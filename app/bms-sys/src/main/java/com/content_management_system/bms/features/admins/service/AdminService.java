package com.content_management_system.bms.features.admins.service;

import com.content_management_system.bms.features.admins.dto.AdminResponse;
import com.content_management_system.bms.features.admins.dto.CreateAdminRequest;
import com.content_management_system.bms.features.admins.dto.UpdateAdminRequest;

import java.util.List;

public interface AdminService {
    AdminResponse createAdmin(CreateAdminRequest createAdminRequest);
    List<AdminResponse> getAllAdmins();
    AdminResponse getAdminById(Long id);
    AdminResponse updateAdmin(Long id, UpdateAdminRequest updateAdminRequest);
    void deleteAdmin(Long id);
}
