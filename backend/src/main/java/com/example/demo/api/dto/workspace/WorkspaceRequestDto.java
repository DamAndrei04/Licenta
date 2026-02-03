package com.example.demo.api.dto.workspace;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.page.PageRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class WorkspaceRequestDto {

    @NotNull
    private Long projectId;

    @NotNull
    private List<PageRequestDto> pages;

    @NotNull
    private Map<String, List<ComponentRequestDto>> componentsByPageTempId;
}
