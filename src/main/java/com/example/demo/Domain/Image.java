package com.example.demo.Domain;

import com.example.demo.Common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Table(
        name = "image",
        indexes = {
                @Index(name = "idx_project_status_create", columnList = "project_id, status, created_at"),
        }
)
public class Image extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "origin_file_name", nullable = false, length = 255)
    private String originFileName;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl;

    @Column(name = "thumbnail_url", length = 2048)
    private String thumbnailUrl;

    @Column(name = "size", nullable = false)
    private long size;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "soft_delete", nullable = false)
    private Boolean softDelete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.READY;

    @Builder
    public Image(Project project, String originFileName, String contentType, String fileUrl, String thumbnailUrl, long size, String memo, String tags, Boolean softDelete, Status status) {
        this.project = project;
        this.originFileName = originFileName;
        this.contentType = contentType;
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.size = size;
        this.memo = memo;
        this.tags = tags;
        this.softDelete = softDelete;
        this.status = status;
    }
}
