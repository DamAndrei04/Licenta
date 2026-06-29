package com.uibuilder.mas.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uibuilder.mas.api.dto.AgentStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionează emițătoarele SSE indexate după identificatorul de sesiune și oferă un API de
 * emitere bazat pe {@link ThreadLocal}, astfel încât orice bean Spring din lanțul de
 * procesare să poată publica evenimente fără a primi explicit identificatorul de sesiune.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentStatusPublisher {

    private static final long SSE_TIMEOUT_MS = 600_000L; // 10 minutes

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ThreadLocal<String> sessionContext = new ThreadLocal<>();
    private final ObjectMapper objectMapper;

    // ── Subscription ────────────────────────────────────────────────────────

    /**
     * Apelată de endpoint-ul SSE când frontend-ul se conectează. Creează și înregistrează
     * un emițător pentru sesiune, pe care controlerul îl returnează ca răspuns.
     *
     * @param sessionId identificatorul sesiunii care se abonează
     * @return emițătorul SSE asociat sesiunii
     */
    public SseEmitter subscribe(String sessionId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitters.put(sessionId, emitter);
        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> {
            emitters.remove(sessionId);
            log.warn("SSE emitter timed out for session {}", sessionId);
        });
        emitter.onError(e -> {
            emitters.remove(sessionId);
            log.warn("SSE emitter error for session {}: {}", sessionId, e.getMessage());
        });
        log.info("SSE subscribed for session {}", sessionId);
        return emitter;
    }

    // ── Thread context ───────────────────────────────────────────────────────

    /**
     * Leagă un identificator de sesiune de firul de execuție curent.
     *
     * @param sessionId identificatorul de sesiune asociat firului curent
     */
    public void setSession(String sessionId) {
        sessionContext.set(sessionId);
    }

    /**
     * Eliberează sesiunea legată de firul curent. Trebuie apelată (într-un bloc
     * {@code finally}) după finalizarea procesării.
     */
    public void clearSession() {
        sessionContext.remove();
    }

    // ── Emit ─────────────────────────────────────────────────────────────────

    /**
     * Trimite un eveniment de stare către frontend pentru sesiunea curentă. Dacă nu există
     * o sesiune legată de firul curent sau un emițător activ, apelul nu are efect.
     *
     * @param event evenimentul de stare care trebuie publicat
     */
    public void emit(AgentStatusEvent event) {
        String sessionId = sessionContext.get();
        if (sessionId == null) {
            return;
        }
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            emitter.send(SseEmitter.event().name("agent-status").data(json));
            log.debug("SSE event sent [{}]: {}", sessionId, json);
        } catch (IOException e) {
            log.warn("Failed to send SSE event for session {}: {}", sessionId, e.getMessage());
            emitters.remove(sessionId);
        }
    }

    /**
     * Finalizează și elimină emițătorul SSE asociat sesiunii curente, închizând fluxul de
     * evenimente.
     */
    public void complete() {
        String sessionId = sessionContext.get();
        if (sessionId == null) {
            return;
        }
        SseEmitter emitter = emitters.remove(sessionId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("SSE stream completed for session {}", sessionId);
            } catch (Exception e) {
                log.warn("Error completing SSE emitter for session {}: {}", sessionId, e.getMessage());
            }
        }
    }
}
