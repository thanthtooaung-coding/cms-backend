package com.content_management_system.lms.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "\"Student_Answer\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Student_Answer\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class StudentAnswer extends MasterData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_quiz_id")
    @JsonIgnore
    private StudentQuiz studentQuiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    @JsonIgnore
    private Answer answer;
}
