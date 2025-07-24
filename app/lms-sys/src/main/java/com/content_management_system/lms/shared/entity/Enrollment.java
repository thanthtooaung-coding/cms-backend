package com.content_management_system.lms.shared.entity;

import com.content_management_system.lms.shared.constants.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Enrollment\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Enrollment\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Enrollment extends MasterData {

    @Column(name = "enrollment_date")
    private OffsetDateTime enrollmentDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal progress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enrollment_status")
    private EnrollmentStatus status;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Certificate certificate;
}