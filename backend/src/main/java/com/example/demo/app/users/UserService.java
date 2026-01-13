package com.example.demo.app.users;

import com.example.demo.api.dto.user.UpdateUserRequestDto;
import com.example.demo.api.dto.user.UserRequestDto;
import com.example.demo.api.dto.user.UserResponseDto;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.UserAlreadyExistsException;
import com.example.demo.api.exception.UserNotFoundException;
import com.example.demo.app.security.CustomUserDetails;
import com.example.demo.app.users.util.UserConverter;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto){
        if(userRepository.existsByUsername(requestDto.getUsername())){
            throw new UserAlreadyExistsException("Username already in use: " + requestDto.getUsername());
        }

        UserEntity user = new UserEntity();
        updateUserDataWhenCreate(user, requestDto);

        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        return UserConverter.convertToResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(){
        return UserConverter.convertToCurrentUserResponseDto(getCurrentUserEntity());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll().stream().map(UserConverter::convertToResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id){
        UserEntity userEntity =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new UserNotFoundException("User not found with id: " + id));
        return UserConverter.convertToResponseDto(userEntity);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username){
        UserEntity userEntity =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UserNotFoundException("User not found with username: " + username));
        return UserConverter.convertToResponseDto(userEntity);
    }

    public UserEntity getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return userRepository
                .findById(customUserDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public UserResponseDto updateUser(UpdateUserRequestDto updatedUserData, Long id){
        UserEntity user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        validateUserOwnership(user);

        updateUserData(user, updatedUserData);

        UserEntity savedUser = userRepository.save(user);
        return UserConverter.convertToResponseDto(savedUser);
    }

    public void deleteUserById(Long userId){
        UserEntity userEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new UserNotFoundException(String.format("User not found with id: " + userId)));
        userRepository.deleteById(userEntity.getId());
    }

    private void validateUserOwnership(UserEntity user) {
        UserEntity currentUser = getCurrentUserEntity();
        if (!(currentUser.getId().equals(user.getId())))
            throw new OwnershipException();
    }

    private void updateUserData(UserEntity user, UpdateUserRequestDto requestDto){
        user.setUsername(requestDto.getUsername());
    }

    private void updateUserDataWhenCreate(UserEntity user, UserRequestDto requestDto){
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    }
}
