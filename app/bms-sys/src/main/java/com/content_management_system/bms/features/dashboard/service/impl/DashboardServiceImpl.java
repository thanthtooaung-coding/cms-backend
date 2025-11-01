package com.content_management_system.bms.features.dashboard.service.impl;

import com.content_management_system.bms.common.enums.Role;
import com.content_management_system.bms.common.repository.jpa.BookingJpaRepository;
import com.content_management_system.bms.common.repository.jpa.MovieJpaRepository;
import com.content_management_system.bms.common.repository.jpa.TheaterJpaRepository;
import com.content_management_system.bms.common.repository.jpa.UserJpaRepository;
import com.content_management_system.bms.features.dashboard.dto.DashboardStatsResponse;
import com.content_management_system.bms.features.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserJpaRepository userJpaRepository;
    private final TheaterJpaRepository theaterJpaRepository;
    private final MovieJpaRepository movieJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userJpaRepository.count();
        long totalCinemas = theaterJpaRepository.count();
        long totalMovies = movieJpaRepository.count();
        long totalReservations = bookingJpaRepository.count();

        long totalAdmins = userJpaRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();

        return new DashboardStatsResponse(
                totalUsers,
                totalCinemas,
                totalMovies,
                totalAdmins,
                totalReservations
        );
    }
}
