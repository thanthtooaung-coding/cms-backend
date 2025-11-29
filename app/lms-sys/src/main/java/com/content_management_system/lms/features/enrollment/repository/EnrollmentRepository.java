package com.content_management_system.lms.features.enrollment.repository;

import com.content_management_system.lms.shared.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e " +
            "LEFT JOIN e.student s " +
            "LEFT JOIN e.course c " +
            "LEFT JOIN c.category cat " +
            "WHERE (:studentEmail IS NULL OR s.email = :studentEmail) " +
            "AND (:courseId IS NULL OR c.id = :courseId) " +
            "AND (:categoryId IS NULL OR cat.id = :categoryId)")
    List<Enrollment> search(
            @Param("studentEmail") String studentEmail,
            @Param("courseId") Long courseId,
            @Param("categoryId") Long categoryId);
    
    @Query("SELECT DISTINCT e FROM Enrollment e " +
            "LEFT JOIN FETCH e.course c " +
            "LEFT JOIN FETCH c.category cat " +
            "LEFT JOIN FETCH c.instructor i " +
            "WHERE e.student.id = :studentId AND e.deletedAt IS NULL")
    List<Enrollment> findAllByStudentIdWithCourseDetails(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Enrollment e " +
           "JOIN e.course c " +
           "WHERE c.instructor.id = :instructorId AND e.deletedAt IS NULL AND c.deletedAt IS NULL")
    long countDistinctStudentsByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.deletedAt IS NULL")
    long countByCourseId(@Param("courseId") Long courseId);
}