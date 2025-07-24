package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Review\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Review\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review extends MasterData {

    private String title;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}