package com.example.demo.app.users.util;

import com.example.demo.api.dto.user.UserResponseDto;
import com.example.demo.app.users.UserEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

    public static UserResponseDto convertToResponseDto(UserEntity userEntity){
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .build();
    }

    public static UserResponseDto convertToCurrentUserResponseDto(UserEntity userEntity){
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .build();
    }
}
