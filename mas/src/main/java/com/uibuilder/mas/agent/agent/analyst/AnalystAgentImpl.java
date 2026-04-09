package com.uibuilder.mas.agent.agent.analyst;

import com.uibuilder.mas.agent.agent.analyst.extractor.ConflictDetector;
import com.uibuilder.mas.agent.agent.analyst.extractor.ConstraintExtractor;
import com.uibuilder.mas.agent.agent.analyst.extractor.GoalExtractor;
import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.analyst.model.Conflict;
import com.uibuilder.mas.agent.agent.analyst.model.Constraint;
import com.uibuilder.mas.agent.agent.analyst.model.Goal;
import com.uibuilder.mas.agent.dto.AgentMessage;
import com.uibuilder.mas.agent.memory.Blackboard;
import com.uibuilder.mas.agent.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of AnalystAgent with LLM integration.
 * Orchestrates extraction logic via LLM-powered extractors.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalystAgentImpl implements AnalystAgent {
    
    private final GoalExtractor goalExtractor;
    private final ConstraintExtractor constraintExtractor;
    private final ConflictDetector conflictDetector;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;
    
    private final String agentId = "analyst-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Override
    public AnalyzedUIModel analyze(String userRequirement) {
        log.info("[{}] Starting UI analysis for requirement: '{}'", agentId, userRequirement);
        
        // Delegate to LLM-powered extractors
        List<Goal> goals = goalExtractor.extractGoals(userRequirement);
        log.info("[{}] Extracted {} goals", agentId, goals.size());
        
        List<Constraint> constraints = constraintExtractor.extractConstraints(userRequirement);
        log.info("[{}] Extracted {} constraints", agentId, constraints.size());
        
        List<Conflict> conflicts = conflictDetector.detectConflicts(goals, constraints);
        log.info("[{}] Detected {} conflicts", agentId, conflicts.size());
        
        // Build the analyzed model
        AnalyzedUIModel analyzedModel = AnalyzedUIModel.builder()
                .analysisId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .sourceDescriptor(null) // No descriptor in this flow
                .goals(goals)
                .constraints(constraints)
                .conflicts(conflicts)
                .build();
        
        // Create message for next agent
        Map<String, Object> messagePayload = Map.of(
                "goals", goals.stream().map(g -> Map.of(
                        "id", g.getId(),
                        "type", g.getType().toString(),
                        "description", g.getDescription(),
                        "priority", g.getPriority().toString()
                )).toList(),
                "constraints", constraints.stream().map(c -> Map.of(
                        "id", c.getId(),
                        "type", c.getType(),
                        "description", c.getDescription()
                )).toList()
        );
        
        String messageJson = jsonUtils.toJson(messagePayload);
        
        log.info("\n" + "=".repeat(80));
        log.info("📊 ANALYST AGENT MESSAGE:");
        log.info("=".repeat(80));
        log.info(messageJson);
        log.info("=".repeat(80) + "\n");
        
        // Store in blackboard
        AgentMessage message = AgentMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .senderAgentId(agentId)
                .targetAgentId("planner")
                .type(AgentMessage.MessageType.ANALYSIS_COMPLETE)
                .timestamp(Instant.now())
                .payload(messagePayload)
                .build();
        
        blackboard.getAnalysisMemory().storeAnalysis(analyzedModel);
        blackboard.storeMessage(message);
        
        log.info("[{}] Analysis complete", agentId);
        return analyzedModel;
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
}
