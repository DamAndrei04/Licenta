package com.uibuilder.mas.agent.agent.builder;

import com.uibuilder.mas.agent.agent.builder.generator.ComponentGenerator;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentNode;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
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
 * Implementarea implicită a agentului Constructor (Builder), cu integrare LLM. Deleagă
 * generarea efectivă a componentelor către {@link ComponentGenerator} și publică
 * arborele de componente rezultat pe blackboard pentru agentul de validare.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BuilderAgentImpl implements BuilderAgent {

    private final ComponentGenerator componentGenerator;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;

    private final String agentId = "builder-" + UUID.randomUUID().toString().substring(0, 8);

    /**
     * Construiește arborele de componente UI pe baza planului: generează componentele
     * fiecărei pagini (prin apeluri LLM) și stochează un mesaj cu rezultatul pe blackboard
     * pentru agentul de validare.
     *
     * @param plan planul UI produs de agentul Planificator
     * @return arborele de componente UI generat
     */
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
    
    /**
     * Transformă recursiv un nod de componentă într-o reprezentare de tip {@code Map}
     * (id, tip, proprietăți, layout și copii) folosită la serializarea mesajului.
     *
     * @param node nodul de componentă care trebuie transformat
     * @return reprezentarea sub formă de hartă a nodului și a copiilor săi
     */
    private Map<String, Object> buildNodeMap(UIComponentNode node) {
        return Map.of(
                "id", node.getNodeId(),
                "type", node.getComponentType(),
                "properties", node.getProperties() != null ? node.getProperties() : Map.of(),
                "layout", node.getLayout() != null ? node.getLayout() : Map.of(),
                "children", node.getChildren() != null ? 
                        node.getChildren().stream().map(this::buildNodeMap).toList() : List.of()
        );
    }
    
    /**
     * Returnează identificatorul unic al acestei instanțe de agent.
     *
     * @return identificatorul agentului
     */
    @Override
    public String getAgentId() {
        return agentId;
    }
}
