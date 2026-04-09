package com.uibuilder.mas.agent.agent.validator;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.validator.rule.ValidationRule;
import com.uibuilder.mas.agent.dto.AgentMessage;
import com.uibuilder.mas.agent.memory.Blackboard;
import com.uibuilder.mas.agent.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of ValidatorAgent with LLM integration.
 * Delegates validation to rule components.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorAgentImpl implements ValidatorAgent {
    
    private final List<ValidationRule> validationRules;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;
    
    private final String agentId = "validator-" + UUID.randomUUID().toString().substring(0, 8);
    
    @Override
    public ValidationResult validate(UIComponentTree componentTree) {
        log.info("[{}] Validating component tree: {}", agentId, componentTree.getTreeId());
        
        List<String> violations = new ArrayList<>();
        
        // Apply all registered validation rules (if any)
        for (ValidationRule rule : validationRules) {
            List<String> ruleViolations = rule.validate(componentTree);
            violations.addAll(ruleViolations);
        }
        
        // Basic structural validation
        if (componentTree.getPages().isEmpty()) {
            violations.add("Component tree has no pages");
        }
        componentTree.getPages().forEach(page -> {
            if (page.getComponents().isEmpty()) {
                violations.add("Page '" + page.getName() + "' has no components");
            }
        });
        
        boolean isValid = violations.isEmpty();
        
        // Create validation message
        Map<String, Object> messagePayload = Map.of(
                "valid", isValid,
                "violationCount", violations.size(),
                "violations", violations,
                "message", isValid ? "Component tree validation passed" : "Component tree has validation issues"
        );
        
        String messageJson = jsonUtils.toJson(messagePayload);
        
        log.info("\n" + "=".repeat(80));
        log.info(" VALIDATOR AGENT MESSAGE:");
        log.info("=".repeat(80));
        log.info(messageJson);
        log.info("=".repeat(80) + "\n");
        
        // Store in blackboard
        AgentMessage message = AgentMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .senderAgentId(agentId)
                .targetAgentId(null)
                .type(AgentMessage.MessageType.VALIDATION_COMPLETE)
                .timestamp(Instant.now())
                .payload(messagePayload)
                .build();
        
        ValidationResult result = new ValidationResult(isValid, violations);
        blackboard.getValidationMemory().storeValidation(result);
        blackboard.storeMessage(message);
        
        log.info("[{}] Validation complete. Valid: {}, Violations: {}", 
                agentId, isValid, violations.size());
        
        return result;
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
}
