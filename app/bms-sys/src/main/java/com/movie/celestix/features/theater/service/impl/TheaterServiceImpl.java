package com.movie.celestix.features.theater.service.impl;

import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.models.Theater;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jdbc.TheaterJdbcRepository;
import com.movie.celestix.common.repository.jpa.ShowtimeJpaRepository;
import com.movie.celestix.common.repository.jpa.TheaterJpaRepository;
import com.movie.celestix.features.theater.dto.*;
import com.movie.celestix.features.theater.mapper.TheaterMapper;
import com.movie.celestix.features.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterJpaRepository theaterJpaRepository;
    private final TheaterJdbcRepository theaterJdbcRepository;
    private final TheaterMapper theaterMapper;
    private final ShowtimeJpaRepository showtimeJpaRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public TheaterResponse create(final CreateTheaterRequest request, Long tenantId) {
        this.theaterJpaRepository.findByNameAndTenantId(request.name(), tenantId).ifPresent(t -> {
            throw new IllegalStateException("Theater with name " + request.name() + " already exists");
        });
        validateSeatConfiguration(
                request.seatConfiguration(),
                request.premiumSeat(),
                request.regularSeat(),
                request.economySeat(),
                request.basicSeat()
        );
        final Theater theater = theaterMapper.toEntity(request);
        theater.setCapacity(request.seatConfiguration().row() * request.seatConfiguration().column());
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        theater.setTenant(tenant);
        
        final Theater savedTheater = this.theaterJpaRepository.save(theater);
        return this.theaterJdbcRepository.findById(savedTheater.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TheaterResponse retrieveOne(final Long id, Long tenantId) {
        // First verify tenant matches using JPA repository
        final Theater theater = this.theaterJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater with id " + id + " not found"));
        if (theater.getTenant() == null || !theater.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Theater with id " + id + " not found for tenant " + tenantId);
        }
        // Then return the JDBC response for full details
        return this.theaterJdbcRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheaterResponse> retrieveAll(Long tenantId) {
        return this.theaterJpaRepository.findAllByTenantId(tenantId).stream()
                .map(theater -> this.theaterJdbcRepository.findById(theater.getId()))
                .toList();
    }

    @Override
    @Transactional
    public TheaterResponse update(final Long id, final UpdateTheaterRequest request, Long tenantId) {
        final Theater theater = this.theaterJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater with id " + id + " not found"));
        // Verify tenant matches
        if (theater.getTenant() == null || !theater.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Theater with id " + id + " not found for tenant " + tenantId);
        }
        this.theaterJpaRepository.findByNameAndTenantId(request.name(), tenantId).ifPresent(t -> {
            if (!Objects.equals(t.getId(), id)) {
                throw new IllegalStateException("Theater with name " + request.name() + " already exists");
            }
        });
        validateSeatConfiguration(
                request.seatConfiguration(),
                request.premiumSeat(),
                request.regularSeat(),
                request.economySeat(),
                request.basicSeat()
        );
        this.theaterMapper.updateTheaterFromDto(request, theater);
        int totalCapacity = request.seatConfiguration().row() * request.seatConfiguration().column();
        if (theater.getCapacity() != totalCapacity) {
            theater.setCapacity(totalCapacity);
        }
        final Theater updatedTheater = this.theaterJpaRepository.save(theater);
        return theaterMapper.toDto(updatedTheater);
    }

    @Override
    @Transactional
    public void delete(final Long id, Long tenantId) {
        final Theater theater = this.theaterJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater with id " + id + " not found"));
        // Verify tenant matches
        if (theater.getTenant() == null || !theater.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Theater with id " + id + " not found for tenant " + tenantId);
        }
        if (showtimeJpaRepository.existsByTheaterId(id)) {
            throw new IllegalStateException("Cannot delete theater with id " + id + " because it is associated with one or more showtimes");
        }
        this.theaterJpaRepository.deleteById(id);
    }

    private void validateSeatConfiguration(
            final SeatConfigurationData seatConfiguration,
            final SeatInfoData premiumSeat,
            final SeatInfoData regularSeat,
            final SeatInfoData economySeat,
            final SeatInfoData basicSeat
    ) {
        final int totalSeatRows = premiumSeat.totalRows() +
                regularSeat.totalRows() +
                economySeat.totalRows() +
                basicSeat.totalRows();

        if (seatConfiguration.row() != totalSeatRows) {
            throw new IllegalStateException("Total number of rows in seat configuration does not match the sum of rows for all seat types.");
        }
    }
}
