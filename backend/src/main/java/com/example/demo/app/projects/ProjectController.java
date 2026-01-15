package com.example.demo.app.projects;

import com.example.demo.api.ProjectApi;
import com.example.demo.api.dto.project.ProjectRequestDto;
import com.example.demo.api.dto.project.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    @Override
    public ResponseEntity<ProjectResponseDto> createProject(ProjectRequestDto projectRequestDto){
        log.info("Received request for createProject with {}", projectRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(projectRequestDto));
    }

    @Override
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        log.info("Received request for getAllProjects");

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getAllProjects());
    }

    @Override
    public ResponseEntity<ProjectResponseDto> getProjectById(Long projectId){
        log.info("Received request for getProjectById: {}", projectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getProjectById(projectId));
    }

    @Override
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByUserId(Long userId){
        log.info("Received request for getProjectsByUserId: {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getProjectsByUserId(userId));
    }

    @Override
    public ResponseEntity<List<ProjectResponseDto>> getCurrentUserProjects(){
        log.info("Received request for getCurrentUserProjects");

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getCurrentUserProjects());
    }

    @Override
    public ResponseEntity<ProjectResponseDto> updateProject(ProjectRequestDto projectRequestDto, Long projectId){
        log.info("Received request for updateProject with id: {} and {}", projectId, projectRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.updateProject(projectRequestDto, projectId));
    }

    @Override
    public ResponseEntity<Void> deleteProject(Long projectId){
        log.info("Received request for deleteProject with id: {}", projectId);

        projectService.deleteProjectById(projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
