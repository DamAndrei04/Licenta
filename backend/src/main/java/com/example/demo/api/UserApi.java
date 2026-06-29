package com.example.demo.api;

import com.example.demo.api.dto.user.UpdateUserRequestDto;
import com.example.demo.api.dto.user.UserRequestDto;
import com.example.demo.api.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contractul REST pentru gestionarea utilizatorilor (rădăcina {@code /user}).
 * Implementarea este asigurată de {@link com.example.demo.app.users.UserController}.
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User")
public interface UserApi {

    /**
     * Înregistrează un utilizator nou (HTTP POST {@code /user}).
     *
     * @param userRequestDto datele de înregistrare, validate
     * @return răspuns HTTP 201 cu utilizatorul creat
     */
    @PostMapping
    ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Valid UserRequestDto userRequestDto);

    /**
     * Returnează toți utilizatorii (HTTP GET {@code /user}).
     *
     * @return răspuns HTTP 200 cu lista tuturor utilizatorilor
     */
    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    /**
     * Returnează un utilizator după id (HTTP GET {@code /user/{userId}}).
     *
     * @param userId identificatorul utilizatorului din calea cererii
     * @return răspuns HTTP 200 cu utilizatorul găsit
     */
    @GetMapping("/{userId}")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId);

    /**
     * Returnează utilizatorul curent autenticat (HTTP GET {@code /user/current}).
     *
     * @return răspuns HTTP 200 cu datele utilizatorului autenticat
     */
    @GetMapping("/current")
    ResponseEntity<UserResponseDto> getCurrentUser();

    /**
     * Actualizează un utilizator existent (HTTP PUT {@code /user/{userId}}).
     *
     * @param updateUserRequestDto noile date ale utilizatorului, validate
     * @param userId identificatorul utilizatorului de actualizat
     * @return răspuns HTTP 200 cu utilizatorul actualizat
     */
    @PutMapping("/{userId}")
    ResponseEntity<UserResponseDto> updateUser(
            @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto, @PathVariable Long userId);

    /**
     * Șterge un utilizator (HTTP DELETE {@code /user/{userId}}).
     *
     * @param userId identificatorul utilizatorului de șters
     * @return răspuns HTTP 204 fără conținut
     */
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable Long userId);

}
