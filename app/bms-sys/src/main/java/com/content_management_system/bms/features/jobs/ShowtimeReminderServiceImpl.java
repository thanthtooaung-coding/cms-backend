package com.content_management_system.bms.features.jobs;

import com.content_management_system.bms.common.models.BookedSeat;
import com.content_management_system.bms.common.models.Booking;
import com.content_management_system.bms.common.models.Configuration;
import com.content_management_system.bms.common.models.Showtime;
import com.content_management_system.bms.common.repository.jpa.ConfigurationJpaRepository;
import com.content_management_system.bms.common.repository.jpa.ShowtimeJpaRepository;
import com.content_management_system.bms.features.email.dto.ReminderEmailDetails;
import com.content_management_system.bms.features.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeReminderServiceImpl implements ShowtimeReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ShowtimeReminderServiceImpl.class);
    private final ShowtimeJpaRepository showtimeJpaRepository;
    private final EmailService emailService;
    private final ConfigurationJpaRepository configurationJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public void processShowtimeReminders() {
        final LocalTime now = LocalTime.now();
        final Configuration config = configurationJpaRepository.findByCode("SHOWTIME_SCHEDULER_MINUTES")
                .orElse(new Configuration("SHOWTIME_SCHEDULER_MINUTES", "10"));
        final int reminderMinutes = Integer.parseInt(config.getValue());
        final LocalTime reminderTime = now.plusMinutes(reminderMinutes);
        logger.info("Running ShowtimeReminderJob at {} for showtimes between {} and {}", now, now, reminderTime);

        final List<Showtime> upcomingShowtimes = showtimeJpaRepository.findByShowtimeDateAndShowtimeTimeBetween(
                LocalDate.now(), now, reminderTime
        );

        if (CollectionUtils.isEmpty(upcomingShowtimes)) {
            logger.info("No upcoming showtimes found in the next {} minutes.", reminderMinutes);
            return;
        }

        for (Showtime showtime : upcomingShowtimes) {
            logger.info("Processing showtime for movie: {} at theater: {}", showtime.getMovie().getTitle(), showtime.getTheater().getName());

            Map<Booking, List<BookedSeat>> bookingsMap = showtime.getBookedSeats().stream()
                    .collect(Collectors.groupingBy(BookedSeat::getBooking));

            for (Booking booking : bookingsMap.keySet()) {
                ReminderEmailDetails details = new ReminderEmailDetails(
                        booking.getUser().getName(),
                        booking.getUser().getEmail(),
                        showtime.getMovie().getTitle(),
                        showtime.getMovie().getMoviePosterUrl(),
                        showtime.getTheater().getName(),
                        showtime.getShowtimeTime()
                );
                emailService.sendShowtimeReminderEmail(details);
            }
        }
    }
}
