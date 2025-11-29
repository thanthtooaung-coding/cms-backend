package com.movie.celestix.features.admins.service;

import com.movie.celestix.features.admins.dto.AdminResponse;
import com.movie.celestix.features.admins.dto.CreateAdminRequest;
import com.movie.celestix.features.admins.dto.UpdateAdminRequest;

import java.util.List;

public interface AdminService {
    AdminResponse createAdmin(CreateAdminRequest createAdminRequest);
    List<AdminResponse> getAllAdmins(Long tenantId);
    AdminResponse getAdminById(Long id, Long tenantId);
    AdminResponse updateAdmin(Long id, UpdateAdminRequest updateAdminRequest, Long tenantId);
    void deleteAdmin(Long id, Long tenantId);
}
