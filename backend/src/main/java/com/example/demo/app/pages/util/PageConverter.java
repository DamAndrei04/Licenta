package com.example.demo.app.pages.util;

import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.app.components.ComponentEntity;
import com.example.demo.app.components.util.ComponentConverter;
import com.example.demo.app.pages.PageEntity;

import java.util.List;

public class PageConverter {

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
