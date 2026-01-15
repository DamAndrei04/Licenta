package com.example.demo.api.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRequestDto {

    @Size(max = 255)
    @NotNull
    private String name;

    @Size(max = 255)
    private String description;

}
