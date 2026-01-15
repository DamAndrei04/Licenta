package com.example.demo.api.dto.page;

import com.example.demo.api.dto.component.ComponentResponseDto;
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
public class PageResponseDto {

    private Long id;

    private Long projectId;

    private String name;

    private String route;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    List<ComponentResponseDto> components;
}
