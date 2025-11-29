package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.constants.CourseStatus;
import com.content_management_system.lms.shared.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT COUNT(c) FROM Course c WHERE c.category.id = :categoryId AND c.deletedAt IS NULL")
    long countByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId AND c.deletedAt IS NULL")
    List<Course> findAllByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.deletedAt IS NULL")
    List<Course> findAllByStatus(@Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.deletedAt IS NULL AND " +
           "(c.category.tenant.id = :tenantId OR :tenantId IS NULL)")
    List<Course> findAllPublishedByTenant(@Param("status") CourseStatus status, @Param("tenantId") Long tenantId);
    
    @Query("SELECT c FROM Course c " +
           "LEFT JOIN FETCH c.modules " +
           "WHERE c.id = :id AND c.deletedAt IS NULL")
    java.util.Optional<Course> findByIdWithModules(@Param("id") Long id);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.deletedAt IS NULL")
    long countByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId AND c.deletedAt IS NULL")
    List<Course> findAllByInstructorId(@Param("instructorId") Long instructorId);
}