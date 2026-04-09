package com.uibuilder.mas.app;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.api.AgentApi;
import com.uibuilder.mas.api.dto.PromptRequestDto;
import com.uibuilder.mas.api.dto.PromptResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AgentController implements AgentApi {

    private final AgentService agentService;

    @Override
    public ResponseEntity<UIDescriptor> sendJsonRepresentation(PromptRequestDto promptRequestDto){
        log.info("Received request for sendJsonRepresentation {}", promptRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(agentService.generateJSON(promptRequestDto));
    }
}
