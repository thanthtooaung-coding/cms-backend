package com.content_management_system.lms.shared.entity;

import com.content_management_system.lms.shared.constants.CourseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "\"Course\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Course\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Course extends MasterData {

    private String title;
    private String description;
    
    @Column(name = "overall_rating")
    private Integer overallRating;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "course_status")
    private CourseStatus status;

    @Column(name = "duration_day_count")
    private Integer durationDayCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CourseCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}