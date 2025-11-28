package com.content_management_system.lms.shared.repository;

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
}