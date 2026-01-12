package com.example.demo.api.dto.user;

import com.example.demo.api.dto.project.ProjectResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponseDto {

    private Long id;

    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private List<ProjectResponseDto> projects;

}
