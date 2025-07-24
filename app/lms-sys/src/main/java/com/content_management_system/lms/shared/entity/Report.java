package com.content_management_system.lms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"Report\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"Report\" SET deleted_at = now() WHERE report_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "generated_date")
    private OffsetDateTime generatedDate;

    @Lob
    @Column(name = "data_snapshot")
    private String dataSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_user_id")
    private User generatedByUser;
}