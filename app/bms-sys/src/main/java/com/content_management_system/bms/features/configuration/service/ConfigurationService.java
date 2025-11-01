package com.content_management_system.bms.features.configuration.service;

import com.content_management_system.bms.common.models.Configuration;
import com.content_management_system.bms.features.configuration.dto.ConfigurationResponse;
import com.content_management_system.bms.features.configuration.dto.UpdateConfigurationRequest;

public interface ConfigurationService {
    ConfigurationResponse getConfigurationByCode(String code);
    void updateConfiguration(String code, UpdateConfigurationRequest request);
    void saveConfiguration(Configuration configuration);
    void updateAndFixShowtimes(String code, UpdateConfigurationRequest request);
}
