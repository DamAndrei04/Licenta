package com.uibuilder.mas.api;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.api.dto.PromptRequestDto;
import com.uibuilder.mas.api.dto.PromptResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/agent")
@Tag(name = "agent")
public interface AgentApi {

    @PostMapping
    ResponseEntity<UIDescriptor> sendJsonRepresentation(@RequestBody @Valid PromptRequestDto promptRequestDto);

    /**
     * SSE endpoint. The frontend subscribes here (EventSource) before sending the
     * POST /agent request. Events are pushed as the pipeline progresses.
     */
    @GetMapping(value = "/status/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter subscribeToStatus(@PathVariable String sessionId);
}
