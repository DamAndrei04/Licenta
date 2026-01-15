package com.example.demo.app.components;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.component.ComponentResponseDto;
import com.example.demo.api.exception.ComponentNotFoundException;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.PageNotFoundException;
import com.example.demo.app.components.util.ComponentConverter;
import com.example.demo.app.pages.PageEntity;
import com.example.demo.app.pages.PageRepository;
import com.example.demo.app.pages.PageService;
import com.example.demo.app.pages.util.PageConverter;
import com.example.demo.app.users.UserEntity;
import com.example.demo.app.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;
    private final PageService pageService;
    private final PageRepository pageRepository;
    private final UserService userService;

    @Transactional
    public ComponentResponseDto createComponent(ComponentRequestDto requestDto, Long pageId) {

        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        pageService.validatePageOwnership(page);

        ComponentEntity component = new ComponentEntity();
        updateComponentData(requestDto, component);

        component.setPage(page);

        if(requestDto.getParentId() != null){
            ComponentEntity parent = componentRepository.findById(requestDto.getParentId())
                    .orElseThrow(
                            () -> new ComponentNotFoundException(String.format("Component with id: %d not found", requestDto.getParentId())));

            component.setParent(parent);
            parent.getChildren().add(component);
        }

        ComponentEntity createdComponent = componentRepository.saveAndFlush(component);
        return ComponentConverter.convertToResponseDto(createdComponent);
    }

    public List<ComponentResponseDto> getAllComponents() {
        return componentRepository.findAll().stream()
                .map(ComponentConverter::convertToResponseDto)
                .toList();
    }

    public ComponentResponseDto getComponentById(Long componentId) {
        ComponentEntity component = componentRepository
                .findById(componentId)
                .orElseThrow(
                        () -> new ComponentNotFoundException(String.format("Component with id: %d not found", componentId)));

        return ComponentConverter.convertToResponseDto(component);
    }

    public List<ComponentResponseDto> getComponentsByPageId(Long pageId) {
        if(!pageRepository.existsById(pageId))
            throw new PageNotFoundException(String.format("Page with id: %d not found", pageId));

        List<ComponentEntity> components = componentRepository.getComponentEntitiesByPageId(pageId);
        return components.stream().map(ComponentConverter::convertToResponseDto).toList();
    }

    public ComponentResponseDto updateComponent(ComponentRequestDto requestDto, Long componentId){
        ComponentEntity component = componentRepository
                .findById(componentId)
                .orElseThrow(
                        () -> new ComponentNotFoundException(String.format("Component with id: %d not found", componentId)));

        pageService.validatePageOwnership(component.getPage());

        updateComponentData(requestDto, component);

        ComponentEntity updatedComponent = componentRepository.saveAndFlush(component);
        return ComponentConverter.convertToResponseDto(updatedComponent);
    }

    public void validateComponentOwnership(ComponentEntity component){
        UserEntity currentUser = userService.getCurrentUserEntity();
        if(!(currentUser.getId().equals(component.getPage().getProject().getUser().getId())))
            throw new OwnershipException();
    }

    private void updateComponentData(ComponentRequestDto requestDto, ComponentEntity component) {
        component.setExternalId(requestDto.getExternalId());
        component.setType(requestDto.getType());
        component.setProps(requestDto.getProps());
        component.setLayout(requestDto.getLayout());
        component.setEvents(requestDto.getEvents());
        component.setState(requestDto.getState());

    }
}
