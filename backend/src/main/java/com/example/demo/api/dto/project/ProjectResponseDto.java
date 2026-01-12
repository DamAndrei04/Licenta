package com.example.demo.api.dto.project;


import com.example.demo.api.dto.page.PageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;

    private Long userId;

    private String name;

    private String description;

    private LocalDateTime updatedAt;

    private List<PageResponseDto> pages;
}
