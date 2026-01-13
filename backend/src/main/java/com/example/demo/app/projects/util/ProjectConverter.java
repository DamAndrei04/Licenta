package com.example.demo.app.projects.util;

import com.example.demo.api.dto.project.ProjectResponseDto;
import com.example.demo.app.pages.util.PageConverter;
import com.example.demo.app.projects.ProjectEntity;
import com.example.demo.app.users.util.UserConverter;

public class ProjectConverter {

    public static ProjectResponseDto convertToResponseDto(ProjectEntity project){
        return ProjectResponseDto.builder()
                .id(project.getId())
                .userId(project.getUser().getId())
                .name(project.getName())
                .description(project.getDescription())
                .updatedAt(project.getUpdatedAt())
                .pages(project.getPages().stream()
                        .map(PageConverter::convertToResponseDto)
                        .toList())
                .build();
    }
}
