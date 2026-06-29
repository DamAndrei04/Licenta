package com.example.demo.app.security;

import com.example.demo.app.users.UserEntity;
import com.example.demo.app.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementare a contractului {@link UserDetailsService} din Spring Security care
 * încarcă utilizatorul din baza de date pentru procesul de autenticare.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Încarcă datele de autenticare ale unui utilizator după numele său de utilizator,
     * construind un {@link CustomUserDetails} folosit de Spring Security.
     *
     * @param username numele de utilizator căutat
     * @return detaliile utilizatorului necesare autenticării
     * @throws UsernameNotFoundException dacă nu există niciun utilizator cu acest nume
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();

        return CustomUserDetails.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .build();
    }
}
