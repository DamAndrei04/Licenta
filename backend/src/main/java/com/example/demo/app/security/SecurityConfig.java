package com.example.demo.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configurația principală de securitate a aplicației (Spring Security). Definește
 * lanțul de filtre HTTP, regulile de autorizare a rutelor, politica de sesiune fără
 * stare și managerul de autenticare.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Configurează lanțul de filtre de securitate: activează CORS, dezactivează CSRF,
     * permite public înregistrarea și documentația Swagger, cere autenticare pentru
     * restul rutelor și folosește sesiuni fără stare cu autenticare HTTP Basic.
     *
     * @param http obiectul de configurare a securității HTTP
     * @return lanțul de filtre de securitate construit
     * @throws Exception dacă apare o eroare la construirea configurației
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/user", "/user/**").permitAll()       // allow POST /user
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/user").permitAll() // swagger
                        .anyRequest().authenticated()                               // everything else requires login
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    /**
     * Definește managerul de autenticare bazat pe un {@link DaoAuthenticationProvider}
     * care folosește serviciul de detalii ale utilizatorului și codificatorul de parole.
     *
     * @return managerul de autenticare configurat
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }
}
