package com.content_management_system.lms.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.content_management_system.lms.shared.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

}
