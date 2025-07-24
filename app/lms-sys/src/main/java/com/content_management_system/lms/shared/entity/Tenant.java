package com.content_management_system.lms.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "\"Tenants\"")
@SQLDelete(sql = "UPDATE \"Tenants\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Tenant extends MasterData {
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "tenant")
    private List<User> users;

    @OneToMany(mappedBy = "tenant")
    private List<CourseCategory> courseCategories;
}
