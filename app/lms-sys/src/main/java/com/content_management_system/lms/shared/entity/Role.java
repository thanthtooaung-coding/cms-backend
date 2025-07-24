package com.content_management_system.lms.shared.entity;

import com.content_management_system.lms.shared.constants.LmsRoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;

@Entity
@Table(name = "\"Role\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Role\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Role extends MasterData {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, columnDefinition = "lms_role_name")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private LmsRoleName name;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}