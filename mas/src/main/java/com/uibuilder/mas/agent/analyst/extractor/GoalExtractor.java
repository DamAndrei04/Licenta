package com.uibuilder.mas.agent.analyst.extractor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uibuilder.mas.agent.analyst.model.Goal;
import com.uibuilder.mas.agent.analyst.model.GoalPriority;
import com.uibuilder.mas.agent.analyst.model.GoalSource;
import com.uibuilder.mas.agent.analyst.model.GoalType;
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
 * Extracts high-level goals from user requirements using LLM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoalExtractor {
    
    private final AnthropicClient anthropicClient;
    private final ObjectMapper objectMapper;
    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;

    public List<Goal> extractGoals(String userRequirement) {
        log.debug("Extracting goals from user requirement using LLM");
        
        String prompt = buildGoalExtractionPrompt(userRequirement);
        String llmResponse = anthropicClient.sendMessage(prompt);
        
        return parseGoalsFromLLMResponse(llmResponse);
    }
    
    private String buildGoalExtractionPrompt(String userRequirement) {

        String template = promptLoader.load("analyst_goal_extraction_v1.md");

        return promptRenderer.render(template, Map.of(
                "USER_REQUIREMENT", userRequirement
        ));


        /*return String.format("""
                You are a UI/UX analyst. Given the following user requirement for a website, extract high-level design goals.
                
                User Requirement: "%s"
                
                Return a JSON array of goals. Each goal should have:
                - type: one of [LAYOUT, INTERACTION, ACCESSIBILITY, RESPONSIVENESS, VISUAL_HIERARCHY, DATA_FLOW, NAVIGATION]
                - description: clear description of the goal
                - priority: one of [CRITICAL, HIGH, MEDIUM, LOW]
                
                Example format:
                [
                  {
                    "type": "LAYOUT",
                    "description": "Create a professional single-page layout with clear sections",
                    "priority": "HIGH"
                  },
                  {
                    "type": "VISUAL_HIERARCHY",
                    "description": "Emphasize name and professional title prominently",
                    "priority": "CRITICAL"
                  }
                ]
                
                Return ONLY the JSON array, no additional text.
                """, userRequirement);*/
    }
    
    private List<Goal> parseGoalsFromLLMResponse(String llmResponse) {
        try {
            // Extract JSON from response (handle potential markdown code blocks)
            String jsonStr = extractJson(llmResponse);
            
            List<Map<String, String>> goalMaps = objectMapper.readValue(
                    jsonStr, 
                    new TypeReference<List<Map<String, String>>>() {}
            );
            
            List<Goal> goals = new ArrayList<>();
            for (Map<String, String> goalMap : goalMaps) {
                Goal goal = Goal.builder()
                        .id(UUID.randomUUID().toString())
                        .type(GoalType.valueOf(goalMap.getOrDefault("type", "LAYOUT")))
                        .description(goalMap.getOrDefault("description", "Unknown goal"))
                        .priority(GoalPriority.valueOf(goalMap.getOrDefault("priority", "MEDIUM")))
                        .source(GoalSource.USER_REQUIREMENT)
                        .contextPageId(null)
                        .contextComponentId(null)
                        .build();
                
                goals.add(goal);
            }
            
            log.info("Successfully parsed {} goals from LLM response", goals.size());
            return goals;
            
        } catch (Exception e) {
            log.error("Failed to parse goals from LLM response", e);
            // Return fallback goal
            return List.of(Goal.builder()
                    .id(UUID.randomUUID().toString())
                    .type(GoalType.LAYOUT)
                    .description("Create UI based on user requirement")
                    .priority(GoalPriority.HIGH)
                    .source(GoalSource.USER_REQUIREMENT)
                    .build());
        }
    }
    
    private String extractJson(String response) {
        // Remove markdown code blocks if present
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
