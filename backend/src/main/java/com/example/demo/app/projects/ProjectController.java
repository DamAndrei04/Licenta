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

/**
 * Controler REST care implementează {@link ProjectApi}. Primește cererile HTTP legate
 * de proiecte, deleagă logica de afaceri către {@link ProjectService} și împachetează
 * rezultatul în răspunsuri HTTP cu codul de stare corespunzător.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    /**
     * {@inheritDoc}
     * Deleagă crearea către serviciu și returnează proiectul creat cu stare 201.
     */
    @Override
    public ResponseEntity<ProjectResponseDto> createProject(ProjectRequestDto projectRequestDto){
        log.info("Received request for createProject with {}", projectRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(projectRequestDto));
    }

    /**
     * {@inheritDoc}
     * Returnează lista tuturor proiectelor cu stare 200.
     */
    @Override
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        log.info("Received request for getAllProjects");

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getAllProjects());
    }

    /**
     * {@inheritDoc}
     * Returnează proiectul identificat prin {@code projectId} cu stare 200.
     */
    @Override
    public ResponseEntity<ProjectResponseDto> getProjectById(Long projectId){
        log.info("Received request for getProjectById: {}", projectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getProjectById(projectId));
    }

    /**
     * {@inheritDoc}
     * Returnează proiectele utilizatorului identificat prin {@code userId} cu stare 200.
     */
    @Override
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByUserId(Long userId){
        log.info("Received request for getProjectsByUserId: {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getProjectsByUserId(userId));
    }

    /**
     * {@inheritDoc}
     * Returnează proiectele utilizatorului curent autenticat cu stare 200.
     */
    @Override
    public ResponseEntity<List<ProjectResponseDto>> getCurrentUserProjects(){
        log.info("Received request for getCurrentUserProjects");

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.getCurrentUserProjects());
    }

    /**
     * {@inheritDoc}
     * Deleagă actualizarea către serviciu și returnează proiectul actualizat cu stare 200.
     */
    @Override
    public ResponseEntity<ProjectResponseDto> updateProject(ProjectRequestDto projectRequestDto, Long projectId){
        log.info("Received request for updateProject with id: {} and {}", projectId, projectRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(projectService.updateProject(projectRequestDto, projectId));
    }

    /**
     * {@inheritDoc}
     * Deleagă ștergerea către serviciu și returnează stare 204 fără conținut.
     */
    @Override
    public ResponseEntity<Void> deleteProject(Long projectId){
        log.info("Received request for deleteProject with id: {}", projectId);

        projectService.deleteProjectById(projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
