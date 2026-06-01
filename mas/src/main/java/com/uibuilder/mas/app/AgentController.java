package com.uibuilder.mas.app;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.api.AgentApi;
import com.uibuilder.mas.api.dto.PromptRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AgentController implements AgentApi {

    private final AgentService agentService;
    private final AgentStatusPublisher statusPublisher;

    @Override
    public ResponseEntity<UIDescriptor> sendJsonRepresentation(PromptRequestDto promptRequestDto) {
        log.info("Received agent request (sessionId={})", promptRequestDto.getSessionId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(agentService.generateJSON(promptRequestDto));
    }

    @Override
    public SseEmitter subscribeToStatus(String sessionId) {
        log.info("SSE subscription request for session {}", sessionId);
        return statusPublisher.subscribe(sessionId);
    }
}
