package com.movie.celestix.common.config;

import com.movie.celestix.common.models.Configuration;
import com.movie.celestix.common.repository.jpa.ConfigurationJpaRepository;
import com.movie.celestix.features.configuration.service.ConfigurationService;
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
            final Configuration configuration = new Configuration();
            configuration.setCode("MAX_BOOKINGS_PER_USER");
            configuration.setValue("10");
            this.configurationService.saveConfiguration(configuration);
        }
        if (this.configurationJpaRepository.findByCode("SHOWTIME_SCHEDULER_MINUTES").isEmpty()) {
            final Configuration configuration = new Configuration();
            configuration.setCode("SHOWTIME_SCHEDULER_MINUTES");
            configuration.setValue("10");
            this.configurationService.saveConfiguration(configuration);
        }
        if (this.configurationJpaRepository.findByCode("CANCELLATION_MINUTES").isEmpty()) {
            final Configuration configuration = new Configuration();
            configuration.setCode("CANCELLATION_MINUTES");
            configuration.setValue("15");
            this.configurationService.saveConfiguration(configuration);
        }
    }
}
