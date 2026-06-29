package com.example.demo.api;

import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contractul REST pentru salvarea și încărcarea stării complete a spațiului de lucru
 * (rădăcina {@code /workspace}). Implementarea este asigurată de
 * {@link com.example.demo.app.workspace.WorkspaceController}.
 */
@RestController
@RequestMapping("/workspace")
@Tag(name = "workspace")
public interface WorkspaceApi {

    /**
     * Salvează starea completă a unui proiect (HTTP POST {@code /workspace}).
     *
     * @param workspaceRequestDto starea workspace-ului (pagini și componente), validată
     * @return răspuns HTTP 201 cu confirmarea salvării
     */
    @PostMapping
    ResponseEntity<WorkspaceResponseDto> saveProjectState(@RequestBody @Valid WorkspaceRequestDto workspaceRequestDto);

    /**
     * Încarcă starea completă a unui proiect (HTTP GET {@code /workspace/{projectId}}).
     *
     * @param projectId identificatorul proiectului a cărui stare se încarcă
     * @return răspuns HTTP 200 cu starea workspace-ului (pagini și componente)
     */
    @GetMapping("/{projectId}")
    ResponseEntity<WorkspaceRequestDto> getProjectWorkspace(@PathVariable Long projectId);
}
