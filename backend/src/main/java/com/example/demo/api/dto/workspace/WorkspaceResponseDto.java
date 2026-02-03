package com.example.demo.api.dto.workspace;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceResponseDto {
    private boolean succes;
    private String message;

}
