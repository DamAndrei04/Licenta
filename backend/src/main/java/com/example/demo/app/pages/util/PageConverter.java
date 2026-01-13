package com.example.demo.app.pages.util;

import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.app.pages.PageEntity;

public class PageConverter {

    public static PageResponseDto convertToResponseDto(PageEntity page){
        return PageResponseDto.builder()
                .id(page.getId())
                .projectId(page.getProject().getId())
                .name(page.getName())
                .route(page.getRoute())
                .updatedAt(page.getUpdatedAt())
                //.components(page.getComponents().stream()
                //        .map(PageConverter::convertToResponseDto)
                //        .toList())
                .build();
    }
}
