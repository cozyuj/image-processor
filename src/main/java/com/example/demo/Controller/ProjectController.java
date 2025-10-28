package com.example.demo.Controller;

import com.example.demo.Service.ProjectService;
import com.example.demo.dto.ProjectReq;
import com.example.demo.dto.ProjectRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/new")
    public ResponseEntity<?> createProject(@RequestBody ProjectReq project) {
        ProjectRes created = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProjectRes> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectRes> updateProject(@PathVariable Long id,
                                                    @RequestBody ProjectReq request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
