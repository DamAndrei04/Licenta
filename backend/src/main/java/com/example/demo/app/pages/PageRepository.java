package com.example.demo.app.pages;

import com.example.demo.app.projects.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository JPA pentru entitatea pagină. Oferă operațiile CRUD standard moștenite din
 * {@link JpaRepository}, plus interogări derivate după proiect.
 */
public interface PageRepository extends JpaRepository<PageEntity, Long> {
    /**
     * Returnează toate paginile unui proiect.
     *
     * @param id identificatorul proiectului
     * @return lista paginilor proiectului
     */
    List<PageEntity> getPageEntitiesByProjectId(Long id);

    /**
     * Șterge toate paginile asociate unui proiect.
     *
     * @param projectId identificatorul proiectului ale cărui pagini se șterg
     */
    void deletePageByProjectId(Long projectId);

    /**
     * Returnează toate paginile unui proiect (variantă derivată după câmpul proiect).
     *
     * @param projectId identificatorul proiectului
     * @return lista paginilor proiectului
     */
    List<PageEntity> findByProjectId(Long projectId);

}
