package com.example.demo.app.users.util;

import com.example.demo.api.dto.user.UserResponseDto;
import com.example.demo.app.projects.util.ProjectConverter;
import com.example.demo.app.users.UserEntity;
import lombok.experimental.UtilityClass;

/**
 * Clasă utilitară de conversie între entitatea utilizator și DTO-ul de răspuns expus
 * prin API.
 */
@UtilityClass
public class UserConverter {

    /**
     * Convertește o entitate utilizator în DTO-ul de răspuns corespunzător, incluzând
     * proiectele utilizatorului convertite.
     *
     * @param userEntity entitatea utilizator care trebuie convertită
     * @return DTO-ul de răspuns corespunzător utilizatorului
     */
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

    /**
     * Convertește entitatea utilizatorului curent în DTO-ul de răspuns corespunzător,
     * incluzând proiectele sale convertite.
     *
     * @param userEntity entitatea utilizatorului curent care trebuie convertită
     * @return DTO-ul de răspuns corespunzător utilizatorului curent
     */
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
