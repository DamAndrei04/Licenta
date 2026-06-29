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

/**
 * Controler REST care implementează {@link UserApi}. Primește cererile HTTP legate de
 * utilizatori, deleagă logica de afaceri către {@link UserService} și împachetează
 * rezultatul în răspunsuri HTTP cu codul de stare corespunzător.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    /**
     * {@inheritDoc}
     * Deleagă înregistrarea către serviciu și returnează utilizatorul creat cu stare 201.
     */
    @Override
    public ResponseEntity<UserResponseDto> createUser(UserRequestDto userRequestDto){
        log.info("Received request for createUser with {}", userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRequestDto));
    }

    /**
     * {@inheritDoc}
     * Returnează lista tuturor utilizatorilor cu stare 200.
     */
    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        log.info("Received request for getAllUsers");

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }

    /**
     * {@inheritDoc}
     * Returnează utilizatorul identificat prin {@code userId} cu stare 200.
     */
    @Override
    public ResponseEntity<UserResponseDto> getUserById( Long userId){
        log.info("Received request for getUserById with id: {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }

    /**
     * {@inheritDoc}
     * Returnează datele utilizatorului curent autenticat cu stare 200.
     */
    @Override
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.info("Received request for getCurrentUser");

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getCurrentUser());
    }

    /**
     * {@inheritDoc}
     * Deleagă actualizarea către serviciu și returnează utilizatorul actualizat cu stare 200.
     */
    @Override
    public ResponseEntity<UserResponseDto> updateUser(UpdateUserRequestDto updateUserRequestDto, Long userId) {
        log.info("Received request for updateUser with id: {} and {}", userId, updateUserRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUser(updateUserRequestDto, userId));
    }

    /**
     * {@inheritDoc}
     * Deleagă ștergerea către serviciu și returnează stare 204 fără conținut.
     */
    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Received request for deleteUser with id: {}", userId);

        userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
