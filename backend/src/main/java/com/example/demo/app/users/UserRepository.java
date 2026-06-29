package com.example.demo.app.users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository JPA pentru entitatea utilizator. Oferă operațiile CRUD standard moștenite
 * din {@link JpaRepository}, plus interogări derivate după numele de utilizator.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Caută un utilizator după numele său de utilizator.
     *
     * @param username numele de utilizator căutat
     * @return un {@link Optional} cu utilizatorul găsit sau gol dacă nu există
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Verifică dacă există deja un utilizator cu numele de utilizator dat.
     *
     * @param username numele de utilizator verificat
     * @return {@code true} dacă numele este deja folosit, altfel {@code false}
     */
    boolean existsByUsername(String username);
}
