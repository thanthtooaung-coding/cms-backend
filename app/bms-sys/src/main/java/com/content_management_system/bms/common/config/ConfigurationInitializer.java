package com.content_management_system.bms.common.config;

import com.content_management_system.bms.common.models.Configuration;
import com.content_management_system.bms.common.repository.jpa.ConfigurationJpaRepository;
import com.content_management_system.bms.features.configuration.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigurationInitializer implements CommandLineRunner {

    private final ConfigurationService configurationService;
    private final ConfigurationJpaRepository configurationJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (this.configurationJpaRepository.findByCode("MAX_BOOKINGS_PER_USER").isEmpty()) {
            final Configuration configuration = new Configuration(
                    "MAX_BOOKINGS_PER_USER",
                    "10"
            );
            this.configurationService.saveConfiguration(configuration);
        }
        if (this.configurationJpaRepository.findByCode("SHOWTIME_SCHEDULER_MINUTES").isEmpty()) {
            final Configuration configuration = new Configuration(
                    "SHOWTIME_SCHEDULER_MINUTES",
                    "10"
            );
            this.configurationService.saveConfiguration(configuration);
        }
        if (this.configurationJpaRepository.findByCode("CANCELLATION_MINUTES").isEmpty()) {
            final Configuration configuration = new Configuration(
                    "CANCELLATION_MINUTES",
                    "15"
            );
            this.configurationService.saveConfiguration(configuration);
        }
    }
}
