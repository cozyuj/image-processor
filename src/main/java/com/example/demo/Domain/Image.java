package com.example.demo.Domain;

import com.example.demo.Common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
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
    private String contectType;

    @Column(name = "size", nullable = false)
    private long size;

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Lob
    @Column(name = "thumbnail", columnDefinition = "LONGBLOB")
    private byte[] thumbnail;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "soft_delete", nullable = false)
    private Boolean softDelete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.READY;

}
