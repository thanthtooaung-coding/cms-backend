package com.movie.celestix.common.config;

import com.movie.celestix.common.repository.jpa.ConfigurationJpaRepository;
import com.movie.celestix.features.jobs.ShowtimeReminderJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final ConfigurationJpaRepository configurationJpaRepository;

    @Bean
    public JobDetail showtimeReminderJobDetail() {
        return JobBuilder.newJob(ShowtimeReminderJob.class)
                .withIdentity("showtimeReminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger showtimeReminderJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(showtimeReminderJobDetail())
                .withIdentity("showtimeReminderTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(getCronExpressionFromDB()))
                .build();
    }

    private String getCronExpressionFromDB() {
        final com.movie.celestix.common.models.Configuration config = configurationJpaRepository.findByCode("SHOWTIME_SCHEDULER_MINUTES")
                .orElseGet(() -> {
                    com.movie.celestix.common.models.Configuration newConfig = new com.movie.celestix.common.models.Configuration();
                    newConfig.setCode("SHOWTIME_SCHEDULER_MINUTES");
                    newConfig.setValue("10");
                    return newConfig;
                });
        final String minutes = config.getValue();
        return "0 0/" + minutes + " * * * ?";
    }
}
