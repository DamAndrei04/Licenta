package com.example.demo.api;

import com.example.demo.api.dto.project.ProjectRequestDto;
import com.example.demo.api.dto.project.ProjectResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contractul REST pentru gestionarea proiectelor (rădăcina {@code /project}).
 * Definește operațiile HTTP expuse clientului, implementarea fiind asigurată de
 * {@link com.example.demo.app.projects.ProjectController}.
 */
@RestController
@RequestMapping("/project")
@Tag(name = "project")
public interface ProjectApi {

    /**
     * Creează un proiect nou pentru utilizatorul curent (HTTP POST {@code /project}).
     *
     * @param projectRequestDto datele proiectului de creat, validate
     * @return răspuns HTTP 201 cu proiectul creat
     */
    @PostMapping
    ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody @Valid ProjectRequestDto projectRequestDto);

    /**
     * Returnează toate proiectele (HTTP GET {@code /project}).
     *
     * @return răspuns HTTP 200 cu lista tuturor proiectelor
     */
    @GetMapping
    ResponseEntity<List<ProjectResponseDto>> getAllProjects();

    /**
     * Returnează un proiect după id (HTTP GET {@code /project/{projectId}}).
     *
     * @param projectId identificatorul proiectului din calea cererii
     * @return răspuns HTTP 200 cu proiectul găsit
     */
    @GetMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long projectId);

    /**
     * Returnează proiectele unui utilizator (HTTP GET {@code /project/user/{userId}}).
     *
     * @param userId identificatorul utilizatorului din calea cererii
     * @return răspuns HTTP 200 cu lista proiectelor utilizatorului
     */
    @GetMapping("/user/{userId}")
    ResponseEntity<List<ProjectResponseDto>> getProjectsByUserId(@PathVariable Long userId);

    /**
     * Returnează proiectele utilizatorului curent (HTTP GET {@code /project/user/current}).
     *
     * @return răspuns HTTP 200 cu lista proiectelor utilizatorului autenticat
     */
    @GetMapping("/user/current")
    ResponseEntity<List<ProjectResponseDto>> getCurrentUserProjects();

    /**
     * Actualizează un proiect existent (HTTP PUT {@code /project/{projectId}}).
     *
     * @param projectRequestDto noile date ale proiectului, validate
     * @param projectId identificatorul proiectului de actualizat
     * @return răspuns HTTP 200 cu proiectul actualizat
     */
    @PutMapping("/{projectId}")
    ResponseEntity<ProjectResponseDto> updateProject(
            @RequestBody @Valid ProjectRequestDto projectRequestDto,
            @PathVariable Long projectId);

    /**
     * Șterge un proiect (HTTP DELETE {@code /project/{projectId}}).
     *
     * @param projectId identificatorul proiectului de șters
     * @return răspuns HTTP 204 fără conținut
     */
    @DeleteMapping("/{projectId}")
    ResponseEntity<Void> deleteProject(@PathVariable Long projectId);
}
