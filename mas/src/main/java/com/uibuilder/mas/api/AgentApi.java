package com.uibuilder.mas.api;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.api.dto.PromptRequestDto;
import com.uibuilder.mas.api.dto.PromptResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
@Tag(name = "agent")
public interface AgentApi {

    @PostMapping
    ResponseEntity<UIDescriptor> sendJsonRepresentation(@RequestBody @Valid PromptRequestDto promptRequestDto);
}
