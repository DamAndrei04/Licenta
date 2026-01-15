package com.example.demo.api;

import com.example.demo.api.dto.user.UpdateUserRequestDto;
import com.example.demo.api.dto.user.UserRequestDto;
import com.example.demo.api.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "User")
public interface UserApi {

    @PostMapping
    ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Valid UserRequestDto userRequestDto);

    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @GetMapping("/{userId}")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId);

    @GetMapping("/current")
    ResponseEntity<UserResponseDto> getCurrentUser();

    @PutMapping("/{userId}")
    ResponseEntity<UserResponseDto> updateUser(
            @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto, @PathVariable Long userId);

    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable Long userId);

}
