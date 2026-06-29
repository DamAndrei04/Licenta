package com.uibuilder.mas.agent.memory;

import com.uibuilder.mas.agent.dto.AgentMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Memoria partajată (blackboard) folosită pentru comunicarea dintre agenți. Stochează,
 * pe segmente, rezultatele fazelor (analiză, planificare, validare) și mesajele schimbate.
 * Stocarea este în memorie, fără persistență.
 */
@Slf4j
@Getter
@Component
public class Blackboard {

    private final AnalysisMemory analysisMemory = new AnalysisMemory();
    private final PlanningMemory planningMemory = new PlanningMemory();
    private final ValidationMemory validationMemory = new ValidationMemory();
    private final List<AgentMessage> messages = new ArrayList<>();

    /**
     * Stochează un mesaj emis de un agent.
     *
     * @param message mesajul care trebuie stocat
     */
    public void storeMessage(AgentMessage message) {
        log.debug("Storing message from {} to {}", message.getSenderAgentId(), message.getTargetAgentId());
        messages.add(message);
    }

    /**
     * Returnează toate mesajele stocate.
     *
     * @return o copie a listei de mesaje
     */
    public List<AgentMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * Golește toate segmentele de memorie și lista de mesaje.
     */
    public void clear() {
        log.info("Clearing blackboard memory");
        analysisMemory.clear();
        planningMemory.clear();
        validationMemory.clear();
        messages.clear();
    }
}
