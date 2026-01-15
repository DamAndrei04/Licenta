package com.example.demo.api;

import com.example.demo.api.dto.project.ProjectRequestDto;
import com.example.demo.api.dto.project.ProjectResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@Tag(name = "project")
public interface ProjectApi {

    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody @Valid ProjectRequestDto projectRequestDto);

    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    @GetMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long projectId);

    @GetMapping("/user/{userId}")
    ResponseEntity<List<ProjectResponseDto>> getProjectsByUserId(@PathVariable Long userId);

    @GetMapping("/user/current")
    ResponseEntity<List<ProjectResponseDto>> getCurrentUserProjects();

    @PutMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> updateProject(
            @RequestBody @Valid ProjectRequestDto projectRequestDto,
            @PathVariable Long projectId);

    @DeleteMapping("/{projectId}")
    ResponseEntity<Void> deleteProject(@PathVariable Long projectId);
}
