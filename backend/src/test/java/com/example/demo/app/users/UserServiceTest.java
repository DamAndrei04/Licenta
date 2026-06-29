package com.example.demo.app.users;

import com.example.demo.api.dto.user.UserRequestDto;
import com.example.demo.api.dto.user.UserResponseDto;
import com.example.demo.api.exception.UserAlreadyExistsException;
import com.example.demo.api.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Basic unit tests for {@link UserService} using Mockito.
 * The repository and password encoder are mocked, so no database or real
 * hashing is involved.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDto request() {
        return UserRequestDto.builder()
                .username("john")
                .password("password123")
                .build();
    }

    @Test
    void createUser_encodesPasswordAndSaves() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("ENCODED");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        UserResponseDto result = userService.createUser(request());

        assertEquals("john", result.getUsername());
        assertEquals(1L, result.getId());
        verify(passwordEncoder, atLeastOnce()).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_whenUsernameTaken_throwsAndDoesNotSave() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_whenExists_returnsDto() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("john");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(1L);

        assertEquals("john", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_whenMissing_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }
}
