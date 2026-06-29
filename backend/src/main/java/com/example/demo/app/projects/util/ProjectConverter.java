package com.example.demo.app.projects.util;

import com.example.demo.api.dto.project.ProjectResponseDto;
import com.example.demo.app.pages.util.PageConverter;
import com.example.demo.app.projects.ProjectEntity;
import com.example.demo.app.users.util.UserConverter;

/**
 * Clasă utilitară de conversie între entitatea proiect și DTO-ul de răspuns expus
 * prin API.
 */
public class ProjectConverter {

    /**
     * Convertește o entitate proiect în DTO-ul de răspuns corespunzător, incluzând
     * datele proprietarului și lista paginilor convertite.
     *
     * @param project entitatea proiect care trebuie convertită
     * @return DTO-ul de răspuns corespunzător proiectului
     */
    public static ProjectResponseDto convertToResponseDto(ProjectEntity project){
        return ProjectResponseDto.builder()
                .id(project.getId())
                .userId(project.getUser().getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .pages(project.getPages().stream()
                        .map(PageConverter::convertToResponseDto)
                        .toList())
                .build();
    }
}
