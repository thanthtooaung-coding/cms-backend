package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmail(String email);
    
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN FETCH b.bookedSeats bs " +
           "LEFT JOIN FETCH bs.showtime s " +
           "LEFT JOIN FETCH s.movie m " +
           "LEFT JOIN FETCH s.theater t " +
           "LEFT JOIN FETCH b.user u " +
           "WHERE b.user.tenant.id = :tenantId")
    List<Booking> findAllByUserTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN FETCH b.bookedSeats bs " +
           "LEFT JOIN FETCH bs.showtime s " +
           "LEFT JOIN FETCH s.movie m " +
           "LEFT JOIN FETCH s.theater t " +
           "LEFT JOIN FETCH b.user u " +
           "WHERE b.user.email = :email AND b.user.tenant.id = :tenantId")
    List<Booking> findByUserEmailAndUserTenantId(@Param("email") String email, @Param("tenantId") Long tenantId);
    
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.user.tenant.id = :tenantId")
    Optional<Booking> findByIdAndUserTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.tenant.id = :tenantId")
    long countByUserTenantId(@Param("tenantId") Long tenantId);
}
