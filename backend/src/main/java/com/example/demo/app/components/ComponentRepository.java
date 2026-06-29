package com.example.demo.app.components;

import com.example.demo.app.pages.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository JPA pentru entitatea componentă. Oferă operațiile CRUD standard moștenite
 * din {@link JpaRepository}, plus interogări derivate după pagină.
 */
public interface ComponentRepository extends JpaRepository<ComponentEntity, Long> {
    /**
     * Returnează toate componentele unei pagini.
     *
     * @param id identificatorul paginii
     * @return lista componentelor paginii
     */
    List<ComponentEntity> getComponentEntitiesByPageId(Long id);

    //Long page(PageEntity page);

    //void deleteComponentByProjectId(Long projectId);

    /**
     * Șterge toate componentele care aparțin paginilor cu identificatorii dați.
     *
     * @param pageIds lista identificatorilor de pagini ale căror componente se șterg
     */
    void deleteByPageIdIn(List<Long> pageIds);
}
