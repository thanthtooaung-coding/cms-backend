package com.movie.celestix.features.jobs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowtimeReminderJob extends QuartzJobBean {

    private final ShowtimeReminderService showtimeReminderService;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        showtimeReminderService.processShowtimeReminders();
    }
}
