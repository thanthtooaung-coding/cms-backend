package com.content_management_system.lms.shared.entity;

import com.content_management_system.lms.shared.constants.MaterialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "\"Lesson\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Lesson\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Lesson extends MasterData {

    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", columnDefinition = "material_type")
    private MaterialType materialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;
}