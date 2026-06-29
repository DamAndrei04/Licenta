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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviciu care gestionează starea completă a spațiului de lucru (workspace) al unui
 * proiect: salvarea întregii structuri de pagini și componente trimise din editorul
 * drag&drop și reconstruirea acesteia pentru încărcare. Reconstituie ierarhia
 * părinte-copil a componentelor pe baza identificatorilor externi din frontend.
 */
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final PageService pageService;
    private final ComponentService componentService;
    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;

    /**
     * Salvează starea completă a unui proiect: șterge paginile existente și
     * recreează paginile și componentele primite, refăcând ierarhia părinte-copil a
     * componentelor pe niveluri de adâncime.
     *
     * @param dto starea workspace-ului trimisă din frontend (proiect, pagini și
     *            elementele plasate pe fiecare pagină)
     * @return DTO de răspuns care confirmă succesul operației de salvare
     * @throws ProjectNotFoundException dacă proiectul nu există
     * @throws PageNotFoundException dacă o pagină nou creată nu poate fi regăsită
     * @throws IllegalStateException dacă se detectează o referință părinte circulară
     *                               sau ruptă între componente
     */
    @Transactional
    public WorkspaceResponseDto saveProjectState(WorkspaceRequestDto dto) {

        Long projectId = dto.getProjectId();

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException(
                                String.format("Project with id: %d not found", projectId)));

        pageService.deletePagesByProjectId(projectId);
        pageRepository.flush();
        //componentService.deleteComponentByProjectId(projectId);
        //pageService.deletePagesByProjectId(projectId);

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

    /**
     * Reconstruiește starea workspace-ului unui proiect pentru a fi încărcată în
     * editor: extrage paginile și componentele din baza de date și reconstituie
     * relațiile părinte-copil sub forma așteptată de frontend.
     *
     * @param projectId identificatorul proiectului a cărui stare se încarcă
     * @return DTO-ul care conține paginile și elementele plasate ale proiectului
     * @throws ProjectNotFoundException dacă proiectul nu există
     */
    @Transactional
    public WorkspaceRequestDto getProjectWorkspace(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException(
                                String.format("Project with id: %d not found", projectId)));

        List<PageEntity> pages = pageRepository.findByProjectId(projectId);

        Map<String, PageImportDto> pageMap = new HashMap<>();
        for (PageEntity page : pages) {
            Map<String, DroppedItemDto> droppedItems = new HashMap<>();

            // First pass — create all items
            for (ComponentEntity comp : page.getComponents()) {
                DroppedItemDto item = new DroppedItemDto();
                item.setId(comp.getExternalId());
                item.setType(comp.getType().name().toLowerCase()); // importJSON expects lowercase
                item.setParentId(comp.getParent() != null ? comp.getParent().getExternalId() : null);
                item.setProps(comp.getProps());
                item.setLayout(comp.getLayout());
                item.setEvents(comp.getEvents());
                item.setState(comp.getState());
                item.setChildrenIds(new ArrayList<>()); // initialize empty
                droppedItems.put(comp.getExternalId(), item);
            }

            // Second pass — rebuild childrenIds from parentId relationships
            for (DroppedItemDto item : droppedItems.values()) {
                if (item.getParentId() != null) {
                    DroppedItemDto parent = droppedItems.get(item.getParentId());
                    if (parent != null) {
                        parent.getChildrenIds().add(item.getId());
                    }
                }
            }

            PageImportDto pageDto = new PageImportDto();
            pageDto.setName(page.getName());
            pageDto.setRoute(page.getRoute());
            pageDto.setDroppedItems(droppedItems);
            pageMap.put(page.getName(), pageDto);
        }

        return WorkspaceRequestDto.builder()
                .projectId(projectId)
                .pages(pageMap)
                .build();
    }

    /**
     * Convertește un element plasat (DTO din frontend) într-o entitate componentă
     * asociată unei pagini, copiind tipul, proprietățile, layout-ul, evenimentele și
     * starea. Relația cu părintele este stabilită separat de apelant.
     *
     * @param dto elementul plasat provenit din editor
     * @param page pagina căreia îi va aparține componenta
     * @return entitatea componentă rezultată, gata de persistare
     */
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
