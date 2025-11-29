package com.movie.celestix.features.moviegenres.service.impl;

import com.movie.celestix.common.exception.ResourceNotFoundException;
import com.movie.celestix.common.models.MovieGenre;
import com.movie.celestix.common.models.Tenant;
import com.movie.celestix.common.repository.TenantRepository;
import com.movie.celestix.common.repository.jpa.MovieGenreJpaRepository;
import com.movie.celestix.common.repository.jpa.MovieJpaRepository;
import com.movie.celestix.features.moviegenres.dto.MovieGenreResponse;
import com.movie.celestix.features.moviegenres.dto.CreateMovieGenreRequest;
import com.movie.celestix.features.moviegenres.dto.UpdateMovieGenreRequest;
import com.movie.celestix.features.moviegenres.mapper.MovieGenreMapper;
import com.movie.celestix.features.moviegenres.service.MovieGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieGenreServiceImpl implements MovieGenreService {

    private final MovieGenreJpaRepository movieGenreJpaRepository;
    private final MovieGenreMapper movieGenreMapper;
    private final MovieJpaRepository movieJpaRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public MovieGenreResponse create(CreateMovieGenreRequest request, Long tenantId) {
        final MovieGenre movieGenre = new MovieGenre();
        movieGenre.setName(request.name());
        movieGenre.setDescription(request.description());
        
        // Set tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        movieGenre.setTenant(tenant);
        
        final MovieGenre savedMovieGenre = this.movieGenreJpaRepository.save(movieGenre);
        return this.movieGenreMapper.toDto(savedMovieGenre);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieGenreResponse retrieveOne(Long id, Long tenantId) {
        final MovieGenre movieGenre = this.movieGenreJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre with id " + id + " not found"));
        // Verify tenant matches
        if (movieGenre.getTenant() == null || !movieGenre.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Genre with id " + id + " not found for tenant " + tenantId);
        }
        return this.movieGenreMapper.toDto(movieGenre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieGenreResponse> retrieveAll(Long tenantId) {
        final List<MovieGenre> genres = movieGenreJpaRepository.findAllByTenantId(tenantId);
        return this.movieGenreMapper.toDtoList(genres);
    }

    @Override
    @Transactional
    public MovieGenreResponse update(Long id, UpdateMovieGenreRequest request, Long tenantId) {
        final MovieGenre movieGenre = this.movieGenreJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre with id " + id + " not found"));
        // Verify tenant matches
        if (movieGenre.getTenant() == null || !movieGenre.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Genre with id " + id + " not found for tenant " + tenantId);
        }
        movieGenre.setName(request.name());
        movieGenre.setDescription(request.description());
        final MovieGenre updatedMovieGenre = this.movieGenreJpaRepository.save(movieGenre);
        return this.movieGenreMapper.toDto(updatedMovieGenre);
    }

    @Override
    @Transactional
    public void delete(Long id, Long tenantId) {
        final MovieGenre movieGenre = this.movieGenreJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre with id " + id + " not found"));
        // Verify tenant matches
        if (movieGenre.getTenant() == null || !movieGenre.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Genre with id " + id + " not found for tenant " + tenantId);
        }
        if (movieJpaRepository.existsByGenres_Id(id)) {
            throw new IllegalStateException("Cannot delete genre with id " + id + " because it is associated with one or more movies");
        }
        this.movieGenreJpaRepository.deleteById(id);
    }
}
