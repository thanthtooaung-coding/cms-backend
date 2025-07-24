package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "\"User\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"User\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends MasterData {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    
    @Column(name = "registration_date")
    private OffsetDateTime registrationDate;

    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToMany(mappedBy = "instructor")
    private List<Course> instructedCourses;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "student")
    private List<StudentQuiz> studentQuizzes;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "student")
    private List<Submission> submissions;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "generatedByUser")
    private List<Report> reports;
}