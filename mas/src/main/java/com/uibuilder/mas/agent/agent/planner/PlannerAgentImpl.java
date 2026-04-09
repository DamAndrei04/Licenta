package com.uibuilder.mas.agent.agent.planner;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.agent.planner.strategy.DefaultPlanningStrategy;
import com.uibuilder.mas.agent.dto.AgentMessage;
import com.uibuilder.mas.agent.memory.Blackboard;
import com.uibuilder.mas.agent.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of PlannerAgent with LLM integration.
 * Delegates planning to LLM-powered strategy components.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlannerAgentImpl implements PlannerAgent {
    
    private final DefaultPlanningStrategy planningStrategy;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;
    
    private final String agentId = "planner-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Override
    public UIPlan createPlan(AnalyzedUIModel analyzedModel) {
        log.info("[{}] Creating execution plan from analysis: {}", agentId, analyzedModel.getAnalysisId());
        
        UIPlan plan = planningStrategy.generatePlan(analyzedModel);
        
        // Create message for next agent
        Map<String, Object> messagePayload = Map.of(
                "planId", plan.getPlanId(),
                "pageCount", plan.getPages().size(),
                "pages", plan.getPages().stream().map(page -> Map.of(
                        "name", page.getName(),
                        "route", page.getRoute(),
                        "stepCount", page.getSteps().size()
                )).toList()
        );
        
        String messageJson = jsonUtils.toJson(messagePayload);
        
        log.info("\n" + "=".repeat(80));
        log.info("📋 PLANNER AGENT MESSAGE:");
        log.info("=".repeat(80));
        log.info(messageJson);
        log.info("=".repeat(80) + "\n");
        
        // Store in blackboard
        AgentMessage message = AgentMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .senderAgentId(agentId)
                .targetAgentId("builder")
                .type(AgentMessage.MessageType.PLANNING_COMPLETE)
                .timestamp(Instant.now())
                .payload(messagePayload)
                .build();
        
        blackboard.getPlanningMemory().storePlan(plan);
        blackboard.storeMessage(message);
        
        log.info("[{}] Plan created with {} pages", agentId, plan.getPages().size());
        return plan;
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
}
