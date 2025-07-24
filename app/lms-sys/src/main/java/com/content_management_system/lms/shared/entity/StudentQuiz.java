package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "\"Student_Quiz\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Student_Quiz\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class StudentQuiz extends MasterData {

    private Integer score;
    private Integer attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}