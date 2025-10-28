package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ProjectRes {
    private Long id;
    private String name;

    @Builder
    public ProjectRes(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
