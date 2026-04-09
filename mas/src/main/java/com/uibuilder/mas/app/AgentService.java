package com.uibuilder.mas.app;

import com.example.demo.api.dto.component.ComponentRequestDto;
import com.example.demo.api.dto.component.enums.ComponentType;
import com.example.demo.api.dto.page.PageRequestDto;
import com.uibuilder.mas.agent.agent.orchestrator.AgentExecutionContext;
import com.uibuilder.mas.agent.agent.orchestrator.AgentOrchestrator;
import com.uibuilder.mas.agent.agent.validator.ValidationResult;
import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.agent.util.SchemaTransformer;
import com.uibuilder.mas.api.dto.PromptRequestDto;
import com.uibuilder.mas.api.dto.PromptResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentOrchestrator agentOrchestrator;
    private final SchemaTransformer schemaTransformer;

    public UIDescriptor generateJSON(PromptRequestDto promptRequestDto){
        AgentExecutionContext context = agentOrchestrator.execute(promptRequestDto.getPrompt());
        return schemaTransformer.transform(context.getComponentTree());
        /*
        UIDescriptor finalDescriptor = schemaTransformer.transform(context.getComponentTree());

        List<PageRequestDto> pages = finalDescriptor.getPages().entrySet().stream()
                .map( e -> PageRequestDto.builder()
                        .name(e.getKey())
                        .route(e.getValue().getRoute())
                        .build())
                .toList();

        Map<String, List<ComponentRequestDto>> componentsByPageTempId = finalDescriptor.getPages().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getDroppedItems().entrySet().stream()
                                .map(c -> ComponentRequestDto.builder()
                                        .externalId(c.getKey())
                                        .parentExternalId(c.getValue().getParentId())
                                        .type(ComponentType.valueOf(c.getValue().getType().toUpperCase()))
                                        .layout(c.getValue().getLayout())
                                        .props(c.getValue().getProps())
                                        .build())
                                .toList()
                ));


        return PromptResponseDto.builder()
                .pages(pages)
                .componentsByPageTempId(componentsByPageTempId)
                .build();


         */
    }
}
