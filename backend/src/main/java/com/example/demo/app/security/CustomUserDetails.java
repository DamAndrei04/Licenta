package com.example.demo.app.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementare a contractului {@link UserDetails} din Spring Security care reține
 * datele utilizatorului autenticat (id, nume de utilizator, parolă) folosite în
 * contextul de securitate.
 */
@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    /**
     * Returnează rolurile (autoritățile) utilizatorului. În aplicație fiecare
     * utilizator primește rolul implicit {@code ROLE_USER}.
     *
     * @return colecția de autorități acordate utilizatorului
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
