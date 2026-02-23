package com.uibuilder.mas.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Message structure for inter-agent communication (via blackboard).
 * Immutable data transfer object.
 */
@Value
@Builder
public class AgentMessage {
    String messageId;
    String senderAgentId;
    String targetAgentId;
    MessageType type;
    Instant timestamp;
    Map<String, Object> payload;
    
    public enum MessageType {
        ANALYSIS_COMPLETE,
        PLANNING_COMPLETE,
        BUILD_COMPLETE,
        VALIDATION_COMPLETE,
        ERROR,
        INFO
    }
}
