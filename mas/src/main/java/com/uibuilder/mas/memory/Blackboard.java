package com.uibuilder.mas.memory;

import com.uibuilder.mas.dto.AgentMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared memory (blackboard) for agent communication.
 * Type-safe, non-persistent storage.
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
     * Store a message from an agent.
     */
    public void storeMessage(AgentMessage message) {
        log.debug("Storing message from {} to {}", message.getSenderAgentId(), message.getTargetAgentId());
        messages.add(message);
    }
    
    /**
     * Get all messages.
     */
    public List<AgentMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * Clear all memory segments.
     */
    public void clear() {
        log.info("Clearing blackboard memory");
        analysisMemory.clear();
        planningMemory.clear();
        validationMemory.clear();
        messages.clear();
    }
}
