package com.uibuilder.mas.agent.builder;

import com.uibuilder.mas.agent.builder.generator.ComponentGenerator;
import com.uibuilder.mas.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.planner.model.UIPlan;
import com.uibuilder.mas.dto.AgentMessage;
import com.uibuilder.mas.memory.Blackboard;
import com.uibuilder.mas.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of BuilderAgent with LLM integration.
 * Delegates component generation to LLM-powered generators.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BuilderAgentImpl implements BuilderAgent {
    
    private final ComponentGenerator componentGenerator;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;
    
    private final String agentId = "builder-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Override
    public UIComponentTree build(UIPlan plan) {
        log.info("[{}] Building UI from plan: {}", agentId, plan.getPlanId());
        
        UIComponentTree tree = componentGenerator.generate(plan);
        
        // Create final JSON representation
        Map<String, Object> messagePayload = Map.of(
                "treeId", tree.getTreeId(),
                "pageCount", tree.getPages().size(),
                "pages", tree.getPages().stream().map(page -> Map.of(
                        "name", page.getName(),
                        "route", page.getRoute(),
                        "componentCount", page.getComponents().size(),
                        "components", page.getComponents().stream()
                                .map(this::buildNodeMap).toList()
                )).toList()
        );
        
        String messageJson = jsonUtils.toJson(messagePayload);
        
        log.info("\n" + "=".repeat(80));
        log.info(" BUILDER AGENT MESSAGE:");
        log.info("=".repeat(80));
        log.info(messageJson);
        log.info("=".repeat(80) + "\n");
        
        // Store in blackboard
        AgentMessage message = AgentMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .senderAgentId(agentId)
                .targetAgentId("validator")
                .type(AgentMessage.MessageType.BUILD_COMPLETE)
                .timestamp(Instant.now())
                .payload(messagePayload)
                .build();
        
        blackboard.storeMessage(message);

        log.info("[{}] Built {} pages", agentId, tree.getPages().size());
        return tree;
    }
    
    private Map<String, Object> buildNodeMap(com.uibuilder.mas.agent.builder.model.UIComponentNode node) {
        return Map.of(
                "id", node.getNodeId(),
                "type", node.getComponentType(),
                "properties", node.getProperties() != null ? node.getProperties() : Map.of(),
                "layout", node.getLayout() != null ? node.getLayout() : Map.of(),
                "children", node.getChildren() != null ? 
                        node.getChildren().stream().map(this::buildNodeMap).toList() : List.of()
        );
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
}
