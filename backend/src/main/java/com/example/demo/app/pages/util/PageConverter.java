package com.example.demo.app.pages.util;

import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.app.components.ComponentEntity;
import com.example.demo.app.components.util.ComponentConverter;
import com.example.demo.app.pages.PageEntity;

import java.util.List;

/**
 * Clasă utilitară de conversie între entitatea pagină și DTO-ul de răspuns expus prin API.
 */
public class PageConverter {

    /**
     * Convertește o entitate pagină în DTO-ul de răspuns corespunzător, incluzând
     * identificatorul proiectului și lista componentelor convertite.
     *
     * @param page entitatea pagină care trebuie convertită
     * @return DTO-ul de răspuns corespunzător paginii
     */
    public static PageResponseDto convertToResponseDto(PageEntity page){

        return PageResponseDto.builder()
                .id(page.getId())
                .projectId(page.getProject().getId())
                .name(page.getName())
                .route(page.getRoute())
                .components(page.getComponents().stream()
                        .map(ComponentConverter::convertToResponseDto)
                        .toList())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }
}
