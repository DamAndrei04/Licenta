package com.example.demo.app.users.util;

import com.example.demo.api.dto.user.UserResponseDto;
import com.example.demo.app.projects.util.ProjectConverter;
import com.example.demo.app.users.UserEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

    public static UserResponseDto convertToResponseDto(UserEntity userEntity){
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .createdAt(userEntity.getCreatedAt())
                .lastLogin(userEntity.getLastLogin())
                .projects(userEntity.getProjects().stream()
                        .map(ProjectConverter::convertToResponseDto)
                        .toList())
                .build();
    }

    public static UserResponseDto convertToCurrentUserResponseDto(UserEntity userEntity){
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .createdAt(userEntity.getCreatedAt())
                .lastLogin(userEntity.getLastLogin())
                .projects(userEntity.getProjects().stream()
                        .map(ProjectConverter::convertToResponseDto)
                        .toList())
                .build();
    }
}
