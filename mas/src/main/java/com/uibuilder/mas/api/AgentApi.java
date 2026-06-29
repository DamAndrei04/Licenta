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

/**
 * Contractul REST al modulului MAS (rădăcina {@code /agent}). Expune generarea interfeței
 * pe baza unui prompt și un canal SSE pentru urmărirea în timp real a progresului
 * pipeline-ului. Implementarea este asigurată de
 * {@link com.uibuilder.mas.app.AgentController}.
 */
@RestController
@RequestMapping("/agent")
@Tag(name = "agent")
public interface AgentApi {

    /**
     * Pornește generarea interfeței pe baza prompt-ului (HTTP POST {@code /agent}).
     *
     * @param promptRequestDto cererea cu prompt-ul și identificatorul de sesiune, validată
     * @return răspuns HTTP 202 cu descriptorul UI generat
     */
    @PostMapping
    ResponseEntity<UIDescriptor> sendJsonRepresentation(@RequestBody @Valid PromptRequestDto promptRequestDto);

    /**
     * Endpoint SSE (HTTP GET {@code /agent/status/{sessionId}}). Frontend-ul se abonează
     * aici (prin EventSource) înainte de a trimite cererea POST {@code /agent}; evenimentele
     * sunt împinse pe măsură ce pipeline-ul avansează.
     *
     * @param sessionId identificatorul sesiunii pentru care se urmărește progresul
     * @return emițătorul SSE pe care sunt publicate evenimentele de stare
     */
    @GetMapping(value = "/status/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter subscribeToStatus(@PathVariable String sessionId);
}
