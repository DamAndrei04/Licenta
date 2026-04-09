package com.uibuilder.mas.api.dto;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PromptResponseDto {

    @NotNull
    private String version;

    @NotNull
    private LocalDate exportedAt;

    @NotNull
    private String activePageId;

    @NotNull
    private List<PageRequestDto> pages;

    @NotNull
    private Map<String, List<ComponentRequestDto>> componentsByPageTempId;
}
