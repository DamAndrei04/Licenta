package com.example.demo.app.workspace;

import com.example.demo.api.WorkspaceApi;
import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WorkspaceController implements WorkspaceApi {

    private final WorkspaceService workspaceService;

    @Override
    public ResponseEntity<WorkspaceResponseDto> saveProjectState(WorkspaceRequestDto workspaceRequestDto){
        log.info("Received request for saveProjectState with {}", workspaceRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workspaceService.saveProjectState(workspaceRequestDto));
    }
}
