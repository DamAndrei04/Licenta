package com.example.demo.app.workspace;

import com.example.demo.api.WorkspaceApi;
import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controler REST care implementează {@link WorkspaceApi}. Primește cererile de salvare
 * și încărcare a stării complete a unui proiect și deleagă către {@link WorkspaceService}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class WorkspaceController implements WorkspaceApi {

    private final WorkspaceService workspaceService;

    /**
     * {@inheritDoc}
     * Deleagă salvarea stării către serviciu și returnează confirmarea cu stare 201.
     */
    @Override
    public ResponseEntity<WorkspaceResponseDto> saveProjectState(WorkspaceRequestDto workspaceRequestDto){
        log.info("Received request for saveProjectState with {}", workspaceRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workspaceService.saveProjectState(workspaceRequestDto));
    }

    /**
     * {@inheritDoc}
     * Deleagă încărcarea stării către serviciu și returnează workspace-ul cu stare 200.
     */
    @Override
    public ResponseEntity<WorkspaceRequestDto> getProjectWorkspace(Long projectId) {
        log.info("Received request for getProjectWorkspace with projectId: {}", projectId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(workspaceService.getProjectWorkspace(projectId));
    }
}
