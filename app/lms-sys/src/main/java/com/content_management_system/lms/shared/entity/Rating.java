package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "\"Rating\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Rating\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Rating extends MasterData {

    @Column(name = "rating_count")
    private Integer ratingCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
}