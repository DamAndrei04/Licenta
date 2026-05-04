package com.example.demo.api;

import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspace")
@Tag(name = "workspace")
public interface WorkspaceApi {

    @PostMapping
    ResponseEntity<WorkspaceResponseDto> saveProjectState(@RequestBody @Valid WorkspaceRequestDto workspaceRequestDto);

    @GetMapping("/{projectId}")
    ResponseEntity<WorkspaceRequestDto> getProjectWorkspace(@PathVariable Long projectId);
}
