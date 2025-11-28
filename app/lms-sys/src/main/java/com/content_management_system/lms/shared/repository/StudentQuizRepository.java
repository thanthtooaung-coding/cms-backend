package com.content_management_system.lms.shared.repository;

import com.content_management_system.lms.shared.entity.StudentQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentQuizRepository extends JpaRepository<StudentQuiz, Long> {
    
    @Query("SELECT sq FROM StudentQuiz sq WHERE sq.student.id = :studentId AND sq.quiz.id = :quizId AND sq.deletedAt IS NULL ORDER BY sq.attempt DESC, sq.createdAt DESC")
    List<StudentQuiz> findAllByStudentIdAndQuizIdOrdered(@Param("studentId") Long studentId, @Param("quizId") Long quizId);
}

