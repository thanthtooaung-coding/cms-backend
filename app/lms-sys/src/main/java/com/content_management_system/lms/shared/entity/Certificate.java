package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Certificate\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Certificate\" SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Certificate extends MasterData {

    @Column(name = "issue_date")
    private OffsetDateTime issueDate;

    @Column(name = "certificate_url", length = 2048)
    private String certificateUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", unique = true)
    private Enrollment enrollment;
}