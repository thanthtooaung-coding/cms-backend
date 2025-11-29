package com.movie.celestix.features.configuration.service;

import com.movie.celestix.common.models.Configuration;
import com.movie.celestix.features.configuration.dto.ConfigurationResponse;
import com.movie.celestix.features.configuration.dto.UpdateConfigurationRequest;

public interface ConfigurationService {
    ConfigurationResponse getConfigurationByCode(String code);
    void updateConfiguration(String code, UpdateConfigurationRequest request);
    void saveConfiguration(Configuration configuration);
    void updateAndFixShowtimes(String code, UpdateConfigurationRequest request);
}
