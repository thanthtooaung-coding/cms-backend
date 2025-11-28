package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByModuleId(Long moduleId);
    
    @Query("SELECT q FROM Quiz q WHERE q.module.id = :moduleId AND q.deletedAt IS NULL")
    List<Quiz> findAllByModuleIdWithSoftDelete(@Param("moduleId") Long moduleId);
}
