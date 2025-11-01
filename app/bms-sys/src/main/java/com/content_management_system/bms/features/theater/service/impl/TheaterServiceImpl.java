package com.content_management_system.bms.features.theater.service.impl;

import com.content_management_system.bms.common.models.Theater;
import com.content_management_system.bms.common.repository.jdbc.TheaterJdbcRepository;
import com.content_management_system.bms.common.repository.jpa.ShowtimeJpaRepository;
import com.content_management_system.bms.common.repository.jpa.TheaterJpaRepository;
import com.content_management_system.bms.features.theater.dto.*;
import com.content_management_system.bms.features.theater.mapper.TheaterMapper;
import com.content_management_system.bms.features.theater.service.TheaterService;
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

    @Override
    @Transactional
    public TheaterResponse create(final CreateTheaterRequest request) {
        this.theaterJpaRepository.findByName(request.name()).ifPresent(t -> {
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
        final Theater savedTheater = this.theaterJpaRepository.save(theater);
        return this.theaterMapper.toDto(savedTheater);
    }

    @Override
    @Transactional(readOnly = true)
    public TheaterResponse retrieveOne(final Long id) {
        return this.theaterJdbcRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheaterResponse> retrieveAll() {
        return this.theaterJdbcRepository.findAll();
    }

    @Override
    @Transactional
    public TheaterResponse update(final Long id, final UpdateTheaterRequest request) {
        final Theater theater = this.theaterJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater with id " + id + " not found"));
        this.theaterJpaRepository.findByName(request.name()).ifPresent(t -> {
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
    public void delete(final Long id) {
        if (!theaterJpaRepository.existsById(id)) {
            throw new RuntimeException("Theater with id " + id + " not found");
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
