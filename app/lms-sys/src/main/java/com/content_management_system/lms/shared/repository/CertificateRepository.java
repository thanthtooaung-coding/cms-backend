package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    @Query("SELECT c FROM Certificate c WHERE c.student.id = :studentId AND c.course.id = :courseId AND c.deletedAt IS NULL")
    Optional<Certificate> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    @Query("SELECT c FROM Certificate c WHERE c.student.id = :studentId AND c.deletedAt IS NULL ORDER BY c.issuedDate DESC")
    List<Certificate> findAllByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Certificate c WHERE c.course.id = :courseId AND c.deletedAt IS NULL")
    List<Certificate> findAllByCourseId(@Param("courseId") Long courseId);
}

