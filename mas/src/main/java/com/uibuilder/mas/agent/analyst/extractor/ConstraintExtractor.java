package com.uibuilder.mas.agent.analyst.extractor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uibuilder.mas.agent.analyst.model.Constraint;
import com.uibuilder.mas.client.AnthropicClient;
import com.uibuilder.mas.prompt.PromptLoader;
import com.uibuilder.mas.prompt.PromptRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Extracts constraints from user requirements using LLM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConstraintExtractor {
    
    private final AnthropicClient anthropicClient;
    private final ObjectMapper objectMapper;
    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;
    
    public List<Constraint> extractConstraints(String userRequirement) {
        log.debug("Extracting constraints from user requirement using LLM");
        
        String prompt = buildConstraintExtractionPrompt(userRequirement);
        String llmResponse = anthropicClient.sendMessage(prompt);
        
        return parseConstraintsFromLLMResponse(llmResponse);
    }
    
    private String buildConstraintExtractionPrompt(String userRequirement) {

        String template = promptLoader.load("analyst_constraint_extraction_v1.md");

        return promptRenderer.render(template, Map.of(
                "USER_REQUIREMENT", userRequirement
        ));

        /*return String.format("""
                You are a UI/UX analyst. Given the following user requirement, extract design constraints and requirements.
                
                User Requirement: "%s"
                
                Return a JSON array of constraints. Each constraint should have:
                - type: constraint category (e.g., "RESPONSIVE", "ACCESSIBILITY", "LAYOUT", "CONTENT")
                - description: clear description of the constraint
                
                Example format:
                [
                  {
                    "type": "RESPONSIVE",
                    "description": "Must be mobile-friendly and responsive"
                  },
                  {
                    "type": "CONTENT",
                    "description": "Include sections for education, experience, and skills"
                  }
                ]
                
                Return ONLY the JSON array, no additional text.
                """, userRequirement);*/
    }
    
    private List<Constraint> parseConstraintsFromLLMResponse(String llmResponse) {
        try {
            String jsonStr = extractJson(llmResponse);
            
            List<Map<String, String>> constraintMaps = objectMapper.readValue(
                    jsonStr,
                    new TypeReference<List<Map<String, String>>>() {}
            );
            
            List<Constraint> constraints = new ArrayList<>();
            for (Map<String, String> constraintMap : constraintMaps) {
                Constraint constraint = Constraint.builder()
                        .id(UUID.randomUUID().toString())
                        .type(constraintMap.getOrDefault("type", "GENERAL"))
                        .description(constraintMap.getOrDefault("description", "Unknown constraint"))
                        .targetComponentId(null)
                        .constraintValue(null)
                        .satisfied(true)
                        .build();
                
                constraints.add(constraint);
            }
            
            log.info("Successfully parsed {} constraints from LLM response", constraints.size());
            return constraints;
            
        } catch (Exception e) {
            log.error("Failed to parse constraints from LLM response", e);
            return List.of(Constraint.builder()
                    .id(UUID.randomUUID().toString())
                    .type("GENERAL")
                    .description("Standard UI best practices")
                    .satisfied(true)
                    .build());
        }
    }
    
    private String extractJson(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}
