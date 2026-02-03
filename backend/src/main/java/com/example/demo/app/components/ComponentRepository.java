package com.example.demo.app.components;

import com.example.demo.app.pages.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<ComponentEntity, Long> {
    List<ComponentEntity> getComponentEntitiesByPageId(Long id);

    //Long page(PageEntity page);

    //void deleteComponentByProjectId(Long projectId);

    void deleteByPageIdIn(List<Long> pageIds);
}
