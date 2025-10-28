package com.example.demo.Service;

import com.example.demo.Domain.Project;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.dto.ProjectReq;
import com.example.demo.dto.ProjectRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectRes createProject(ProjectReq req) {
        Project project = Project.builder()
                .name(req.getName())
                .build();
        Project saved = projectRepository.save(project);
        return new ProjectRes(saved.getId(), saved.getName());
    }

    // 전체 조회
    public List<ProjectRes> getProjects() {
        return projectRepository.findAll().stream()
                .map(p -> new ProjectRes(p.getId(), p.getName()))
                .collect(Collectors.toList());
    }

    // 단건 조회
    public ProjectRes getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return new ProjectRes(project.getId(), project.getName());
    }

    // 수정
    public ProjectRes updateProject(Long id, ProjectReq req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(req.getName());
        Project updated = projectRepository.save(project);
        return new ProjectRes(updated.getId(), updated.getName());
    }

    // 삭제
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }


}
