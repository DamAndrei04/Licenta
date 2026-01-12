package com.example.demo.api.dto.page;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequestDto {

    @NotNull
    private Long projectId;

    @Size(max = 255)
    @NotNull
    private String name;

    @Size(max = 255)
    @NotNull
    private String route;

}
