package com.content_management_system.lms.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.Module;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>  {
    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId AND m.deletedAt IS NULL")
    List<Module> findAllByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT m FROM Module m " +
           "LEFT JOIN FETCH m.lessons " +
           "WHERE m.course.id = :courseId AND m.deletedAt IS NULL")
    List<Module> findAllByCourseIdWithLessons(@Param("courseId") Long courseId);
}
