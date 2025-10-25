package com.example.demo.Domain;

import com.example.demo.Common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Project extends BaseTimeEntity {
    @Id
    private Long id;

    private String name;
}
