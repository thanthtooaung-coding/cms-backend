package com.content_management_system.bms.features.configuration.controller;

import com.content_management_system.bms.common.dto.ApiResponse;
import com.content_management_system.bms.features.configuration.dto.ConfigurationResponse;
import com.content_management_system.bms.features.configuration.dto.UpdateConfigurationRequest;
import com.content_management_system.bms.features.configuration.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<ConfigurationResponse>> getConfiguration(@PathVariable String code) {
        return ApiResponse.ok(configurationService.getConfigurationByCode(code), "Configuration retrieved successfully");
    }

    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> updateConfiguration(@PathVariable String code, @RequestBody UpdateConfigurationRequest request) {
        configurationService.updateConfiguration(code, request);
        return ApiResponse.ok(null, "Configuration updated successfully");
    }

    @PutMapping("/{code}/update-and-fix")
    public ResponseEntity<ApiResponse<Void>> updateAndFix(@PathVariable String code, @RequestBody UpdateConfigurationRequest request) {
        configurationService.updateAndFixShowtimes(code, request);
        return ApiResponse.ok(null, "Configuration updated and showtimes fixed successfully");
    }
}
