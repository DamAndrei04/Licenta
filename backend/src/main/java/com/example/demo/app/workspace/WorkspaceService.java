package com.example.demo.app.workspace;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
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
    private final ComponentRepository componentRepository;
    private final PageRepository pageRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public WorkspaceResponseDto saveProjectState(WorkspaceRequestDto workspaceRequestDto) {

        Long projectId = workspaceRequestDto.getProjectId();
        List<PageRequestDto> pagesRequestDto = workspaceRequestDto.getPages();
        Map<String, List<ComponentRequestDto>> componentsByPageTempId =
                workspaceRequestDto.getComponentsByPageTempId();

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException(String.format("Project with id: %d not found", projectId)));

        componentService.deleteComponentByProjectId(projectId);
        pageService.deletePagesByProjectId(projectId);

        for(PageRequestDto pageRequestDto : pagesRequestDto){
            var pageResponse = pageService.createPage(pageRequestDto, projectId);
            PageEntity page = pageRepository.findById(pageResponse.getId())
                    .orElseThrow(() -> new PageNotFoundException(String.format("Page with id: %d not found", pageResponse.getId())));

            // get all components for this page
            List<ComponentRequestDto> componentRequestDtos =
                    componentsByPageTempId.getOrDefault(pageRequestDto.getName(), List.of());

            Map<String, ComponentEntity> tempIdToEntity = new HashMap<>();

            // create root components first so parents exist in memory
            componentRequestDtos.stream()
                    .filter(dto -> dto.getParentExternalId() == null)
                    .forEach(dto -> {
                        ComponentEntity root = createComponentEntity(dto, page);
                        tempIdToEntity.put(dto.getExternalId(), root);
                        page.getComponents().add(root);
                    });

            // add children iteratively, resolving by depth
            Set<String> unresolved = componentRequestDtos.stream()
                    .filter(dto -> dto.getParentExternalId() != null)
                    .map(ComponentRequestDto::getExternalId)
                    .collect(Collectors.toSet());

            while (!unresolved.isEmpty()) {
                boolean progress = false;
                for (ComponentRequestDto dto : componentRequestDtos) {
                    if (!unresolved.contains(dto.getExternalId())) continue;
                    if (!tempIdToEntity.containsKey(dto.getParentExternalId())) continue;

                    addChildComponent(dto, tempIdToEntity, page);
                    unresolved.remove(dto.getExternalId());
                    progress = true;
                }
                if (!progress) {
                    throw new IllegalStateException("Circular or broken parent reference detected: " + unresolved);
                }
            }

            pageRepository.save(page);
        }

        return WorkspaceResponseDto.builder()
                .succes(true)
                .message("Workspace saved succesfully")
                .build();
    }

    private ComponentEntity createComponentEntity(ComponentRequestDto componentRequestDto, PageEntity page){
        ComponentEntity component = new ComponentEntity();
        component.setExternalId(componentRequestDto.getExternalId());
        component.setType(componentRequestDto.getType());
        component.setProps(componentRequestDto.getProps());
        component.setLayout(componentRequestDto.getLayout());
        component.setEvents(componentRequestDto.getEvents());
        component.setState(componentRequestDto.getState());
        component.setPage(page);
        return component;
    }

    private void addChildComponent(ComponentRequestDto componentRequestDto,
                                              Map<String, ComponentEntity> tempIdToEntity,
                                              PageEntity page) {

        ComponentEntity parent = tempIdToEntity.get(componentRequestDto.getParentExternalId());
        if(parent == null){
            throw new IllegalStateException(
                    "Parent not created yet: " + componentRequestDto.getParentExternalId()
            );
        }

        if (tempIdToEntity.containsKey(componentRequestDto.getExternalId())) {
            return;
        }

        ComponentEntity child = createComponentEntity(componentRequestDto, page);
        child.setParent(parent);
        parent.getChildren().add(child);

        tempIdToEntity.put(child.getExternalId(), child);
    }

}
