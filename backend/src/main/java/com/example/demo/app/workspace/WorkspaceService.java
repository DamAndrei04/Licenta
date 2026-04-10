package com.example.demo.app.workspace;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.component.enums.ComponentType;
import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.api.dto.workspace.DroppedItemDto;
import com.example.demo.api.dto.workspace.PageImportDto;
import com.example.demo.api.dto.workspace.WorkspaceRequestDto;
import com.example.demo.api.dto.workspace.WorkspaceResponseDto;
import com.example.demo.api.exception.PageNotFoundException;
import com.example.demo.api.exception.ProjectNotFoundException;
import com.example.demo.app.components.ComponentEntity;
import com.example.demo.app.components.ComponentRepository;
import com.example.demo.app.components.ComponentService;
import com.example.demo.app.pages.PageEntity;
import com.example.demo.app.pages.PageRepository;
import com.example.demo.app.pages.PageService;
import com.example.demo.app.projects.ProjectEntity;
import com.example.demo.app.projects.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final PageService pageService;
    private final ComponentService componentService;
    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public WorkspaceResponseDto saveProjectState(WorkspaceRequestDto dto) {

        Long projectId = dto.getProjectId();

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException(
                                String.format("Project with id: %d not found", projectId)));

        componentService.deleteComponentByProjectId(projectId);
        pageService.deletePagesByProjectId(projectId);

        for (PageImportDto pageDto : dto.getPages().values()) {

            PageRequestDto pageRequestDto = PageRequestDto.builder()
                    .name(pageDto.getName())
                    .route(pageDto.getRoute())
                    .build();

            var pageResponse = pageService.createPage(pageRequestDto, projectId);
            PageEntity page = pageRepository.findById(pageResponse.getId())
                    .orElseThrow(() ->
                            new PageNotFoundException(
                                    String.format("Page with id: %d not found", pageResponse.getId())));

            Map<String, DroppedItemDto> items = pageDto.getDroppedItems();
            Map<String, ComponentEntity> idToEntity = new HashMap<>();

            // save root components first (parentId == null)
            items.values().stream()
                    .filter(item -> item.getParentId() == null)
                    .forEach(item -> {
                        ComponentEntity root = toEntity(item, page);
                        idToEntity.put(item.getId(), root);
                        page.getComponents().add(root);
                    });

            // resolve children iteratively by depth
            Set<String> unresolved = items.values().stream()
                    .filter(item -> item.getParentId() != null)
                    .map(DroppedItemDto::getId)
                    .collect(Collectors.toSet());

            while (!unresolved.isEmpty()) {
                boolean progress = false;
                for (DroppedItemDto item : items.values()) {
                    if (!unresolved.contains(item.getId())) continue;
                    if (!idToEntity.containsKey(item.getParentId())) continue;

                    ComponentEntity child = toEntity(item, page);
                    ComponentEntity parent = idToEntity.get(item.getParentId());
                    child.setParent(parent);
                    parent.getChildren().add(child);
                    idToEntity.put(item.getId(), child);
                    unresolved.remove(item.getId());
                    progress = true;
                }
                if (!progress) {
                    throw new IllegalStateException(
                            "Circular or broken parent reference detected: " + unresolved);
                }
            }

            pageRepository.save(page);
        }

        return WorkspaceResponseDto.builder()
                .succes(true)
                .message("Workspace saved successfully")
                .build();
    }

    private ComponentEntity toEntity(DroppedItemDto dto, PageEntity page) {
        ComponentEntity entity = new ComponentEntity();
        entity.setExternalId(dto.getId());
        entity.setType(ComponentType.fromString(dto.getType()));
        entity.setProps(dto.getProps());
        entity.setLayout(dto.getLayout());
        entity.setEvents(dto.getEvents());
        entity.setState(dto.getState());
        entity.setPage(page);
        return entity;
    }
}
