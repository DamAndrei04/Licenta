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
 * Manages SSE emitters keyed by session ID and provides a thread-local-based
 * emit API so any Spring bean in the processing chain can publish events
 * without needing the session ID passed explicitly.
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
     * Called by the SSE endpoint when the frontend connects.
     * Returns an emitter the controller hands back as the response body.
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

    /** Binds a session ID to the current processing thread. */
    public void setSession(String sessionId) {
        sessionContext.set(sessionId);
    }

    /** Must be called (in a finally block) after processing completes. */
    public void clearSession() {
        sessionContext.remove();
    }

    // ── Emit ─────────────────────────────────────────────────────────────────

    /** Sends a status event to the frontend for the current session. */
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

    /** Completes and removes the emitter for the current session. */
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
