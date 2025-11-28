package com.content_management_system.lms.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.content_management_system.lms.shared.entity.Lesson;
import com.content_management_system.lms.shared.entity.Module;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByModule(Module module);
    
    @Query("SELECT l FROM Lesson l WHERE l.module.id = :moduleId AND l.deletedAt IS NULL")
    List<Lesson> findAllByModuleId(@Param("moduleId") Long moduleId);
}
