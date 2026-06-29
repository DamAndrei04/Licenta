package com.example.demo.app.projects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository JPA pentru entitatea proiect. Oferă operațiile CRUD standard moștenite
 * din {@link JpaRepository}, plus interogări derivate specifice.
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    /**
     * Returnează toate proiectele care aparțin utilizatorului cu identificatorul dat.
     *
     * @param userId identificatorul utilizatorului proprietar
     * @return lista proiectelor utilizatorului
     */
    List<ProjectEntity> findByUser_Id(Long userId);
}
