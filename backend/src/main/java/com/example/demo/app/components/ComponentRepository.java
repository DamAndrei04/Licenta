package com.example.demo.app.components;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<ComponentEntity, Long> {
    List<ComponentEntity> getComponentEntitiesByPageId(Long id);
}
