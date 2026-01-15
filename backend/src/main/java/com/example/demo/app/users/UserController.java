package com.example.demo.app.users;

import com.example.demo.api.UserApi;
import com.example.demo.api.dto.user.UpdateUserRequestDto;
import com.example.demo.api.dto.user.UserRequestDto;
import com.example.demo.api.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseDto> createUser(UserRequestDto userRequestDto){
        log.info("Received request for createUser with {}", userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRequestDto));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        log.info("Received request for getAllUsers");

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById( Long userId){
        log.info("Received request for getUserById with id: {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.info("Received request for getCurrentUser");

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getCurrentUser());
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(UpdateUserRequestDto updateUserRequestDto, Long userId) {
        log.info("Received request for updateUser with id: {} and {}", userId, updateUserRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUser(updateUserRequestDto, userId));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Received request for deleteUser with id: {}", userId);

        userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
