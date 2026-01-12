package com.example.demo.api.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {

    @Size(min = 10, max = 255)
    @NotNull
    private String username;

    @Size(min = 8, max = 255)
    @NotNull
    private String password;

}
