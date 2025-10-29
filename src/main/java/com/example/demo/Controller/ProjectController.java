package com.example.demo.Controller;

import com.example.demo.Service.ProjectService;
import com.example.demo.dto.ProjectReq;
import com.example.demo.dto.ProjectRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Tag(name = "Project API", description = "프로젝트 관련 기능을 제공하는 Controller")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;


    @Operation(summary = "프로젝트 생성", description = "이름 입력")
    @PostMapping("/new")
    public ResponseEntity<?> createProject(@RequestBody ProjectReq project) {
        ProjectRes created = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "프로젝트 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectRes> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @Operation(summary = "프로젝트 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectRes> updateProject(@PathVariable Long id,
                                                    @RequestBody ProjectReq request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @Operation(summary = "프로젝트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
