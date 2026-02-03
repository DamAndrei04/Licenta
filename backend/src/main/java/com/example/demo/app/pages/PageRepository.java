package com.example.demo.app.pages;

import com.example.demo.app.projects.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<PageEntity, Long> {
    List<PageEntity> getPageEntitiesByProjectId(Long id);
    void deletePageByProjectId(Long projectId);

}
