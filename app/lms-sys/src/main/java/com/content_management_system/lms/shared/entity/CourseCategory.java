package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "\"course_category\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"course_category\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CourseCategory extends MasterData {

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToMany(mappedBy = "category")
    private List<Course> courses;
}