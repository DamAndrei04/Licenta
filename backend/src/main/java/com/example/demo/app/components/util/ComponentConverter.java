package com.example.demo.app.components.util;

import com.example.demo.api.dto.component.ComponentResponseDto;
import com.example.demo.app.components.ComponentEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clasă utilitară de conversie între entitatea componentă și DTO-ul de răspuns expus
 * prin API.
 */
public class ComponentConverter {

    /**
     * Convertește o entitate componentă în DTO-ul de răspuns corespunzător, rezolvând
     * identificatorii externi ai copiilor și ai părintelui.
     *
     * @param component entitatea componentă care trebuie convertită
     * @return DTO-ul de răspuns corespunzător componentei
     */
    public static ComponentResponseDto convertToResponseDto(ComponentEntity component) {

        List<String> childrenIds = component.getChildren() != null
                ? component.getChildren().stream()
                    .map(ComponentEntity::getExternalId)
                    .toList()
                : List.of();


        return ComponentResponseDto.builder()
                .id(component.getId())
                .externalId(component.getExternalId())
                .pageId(component.getPage().getId())
                .parentId(component.getParent() != null ? component.getParent().getId() : null)
                .children(childrenIds)
                .type(component.getType())
                .props(component.getProps())
                .layout(component.getLayout())
                .events(component.getEvents())
                .state(component.getState())
                .createdAt(component.getCreatedAt())
                .updatedAt(component.getUpdatedAt())
                .build();

    }
}
