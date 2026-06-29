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

/**
 * Serviciu care implementează logica de afaceri pentru gestionarea utilizatorilor.
 * Acoperă înregistrarea, citirea, actualizarea și ștergerea utilizatorilor, precum
 * și obținerea utilizatorului curent din contextul de securitate Spring.
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creează (înregistrează) un utilizator nou, verificând în prealabil unicitatea
     * numelui de utilizator și criptând parola înainte de salvare.
     *
     * @param requestDto datele de înregistrare (nume de utilizator și parolă)
     * @return DTO-ul de răspuns corespunzător utilizatorului nou creat
     * @throws UserAlreadyExistsException dacă numele de utilizator este deja folosit
     */
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

    /**
     * Returnează datele utilizatorului curent autenticat.
     *
     * @return DTO-ul de răspuns corespunzător utilizatorului autenticat
     */
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(){
        return UserConverter.convertToCurrentUserResponseDto(getCurrentUserEntity());
    }

    /**
     * Returnează toți utilizatorii existenți în baza de date.
     *
     * @return lista tuturor utilizatorilor sub formă de DTO-uri de răspuns
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll().stream().map(UserConverter::convertToResponseDto).toList();
    }

    /**
     * Caută un utilizator după identificatorul său.
     *
     * @param id identificatorul unic al utilizatorului căutat
     * @return DTO-ul de răspuns corespunzător utilizatorului găsit
     * @throws UserNotFoundException dacă nu există niciun utilizator cu acest id
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id){
        UserEntity userEntity =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new UserNotFoundException("User not found with id: " + id));
        return UserConverter.convertToResponseDto(userEntity);
    }

    /**
     * Caută un utilizator după numele său de utilizator.
     *
     * @param username numele de utilizator căutat
     * @return DTO-ul de răspuns corespunzător utilizatorului găsit
     * @throws UserNotFoundException dacă nu există niciun utilizator cu acest nume
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username){
        UserEntity userEntity =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UserNotFoundException("User not found with username: " + username));
        return UserConverter.convertToResponseDto(userEntity);
    }

    /**
     * Obține entitatea utilizatorului curent pe baza contextului de securitate Spring.
     *
     * @return entitatea utilizatorului autenticat sau {@code null} dacă nu există un
     *         utilizator autenticat în contextul de securitate
     * @throws UserNotFoundException dacă utilizatorul din context nu mai există în baza de date
     */
    public UserEntity getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return null;
        }

        return userRepository
                .findById(customUserDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Actualizează datele unui utilizator existent, după verificarea dreptului de
     * proprietate al utilizatorului curent asupra contului.
     *
     * @param updatedUserData noile date ale utilizatorului
     * @param id identificatorul utilizatorului care trebuie actualizat
     * @return DTO-ul de răspuns corespunzător utilizatorului actualizat
     * @throws UserNotFoundException dacă nu există niciun utilizator cu acest id
     * @throws OwnershipException dacă contul nu aparține utilizatorului curent
     */
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

    /**
     * Șterge un utilizator după identificatorul său.
     *
     * @param userId identificatorul utilizatorului care trebuie șters
     * @throws UserNotFoundException dacă nu există niciun utilizator cu acest id
     */
    public void deleteUserById(Long userId){
        UserEntity userEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new UserNotFoundException(String.format("User not found with id: " + userId)));
        userRepository.deleteById(userEntity.getId());
    }

    /**
     * Verifică dacă contul dat aparține utilizatorului curent autenticat.
     *
     * @param user entitatea utilizator a cărei proprietate se verifică
     * @throws OwnershipException dacă contul nu aparține utilizatorului curent
     */
    private void validateUserOwnership(UserEntity user) {
        UserEntity currentUser = getCurrentUserEntity();
        if (!(currentUser.getId().equals(user.getId())))
            throw new OwnershipException();
    }

    /**
     * Copiază datele actualizabile din DTO în entitatea utilizator (numele de utilizator).
     *
     * @param user entitatea utilizator care va fi actualizată
     * @param requestDto sursa noilor date
     */
    private void updateUserData(UserEntity user, UpdateUserRequestDto requestDto){
        user.setUsername(requestDto.getUsername());
    }

    /**
     * Inițializează datele unui utilizator nou (nume de utilizator și parolă criptată).
     *
     * @param user entitatea utilizator nou creată care va fi populată
     * @param requestDto sursa datelor de înregistrare
     */
    private void updateUserDataWhenCreate(UserEntity user, UserRequestDto requestDto){
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    }
}
