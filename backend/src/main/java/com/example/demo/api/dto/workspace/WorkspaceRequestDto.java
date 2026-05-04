package com.example.demo.api.dto.workspace;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.page.PageRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class WorkspaceRequestDto {
    @NotNull
    private Long projectId;
    private String version;
    private String exportedAt;
    private String activePageId;
    @NotNull
    private Map<String, PageImportDto> pages;
}
