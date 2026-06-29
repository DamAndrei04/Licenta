package com.uibuilder.mas.agent.agent.planner.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.planner.model.PlanStep;
import com.uibuilder.mas.agent.agent.planner.model.UIPage;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.client.AnthropicClient;
import com.uibuilder.mas.agent.prompt.PromptLoader;
import com.uibuilder.mas.agent.prompt.PromptRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Strategia implicită de generare a planurilor de construire a interfeței. Pe baza
 * modelului analizat construiește un prompt, îl trimite către LLM și transformă răspunsul
 * JSON într-un plan structurat (pagini și pași).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultPlanningStrategy {

    private final AnthropicClient anthropicClient;
    private final ObjectMapper objectMapper;
    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;

    /**
     * Generează planul UI pe baza modelului analizat: construiește prompt-ul de
     * planificare, apelează LLM-ul și asamblează planul cu paginile și pașii rezultați.
     *
     * @param analyzedModel modelul UI analizat (obiective și constrângeri)
     * @return planul UI generat
     */
    public UIPlan generatePlan(AnalyzedUIModel analyzedModel) {
        log.debug("Generating plan for analysis: {}", analyzedModel.getAnalysisId());
        
        String prompt = buildPlanningPrompt(analyzedModel);
        String llmResponse = anthropicClient.sendMessage(prompt);
        
        List<UIPage> pages = parsePagesFromLLMResponse(llmResponse);
        
        return UIPlan.builder()
                .planId(UUID.randomUUID().toString())
                .analysisId(analyzedModel.getAnalysisId())
                .createdAt(Instant.now())
                .pages(pages)
                .estimatedComplexity(pages.size())
                .build();
    }
    
    /**
     * Construiește prompt-ul de planificare, formatând obiectivele și constrângerile din
     * modelul analizat și inserându-le în șablonul corespunzător.
     *
     * @param analyzedModel modelul analizat din care se extrag obiectivele și constrângerile
     * @return prompt-ul final trimis către LLM
     */
    private String buildPlanningPrompt(AnalyzedUIModel analyzedModel) {
        String goalsStr = analyzedModel.getGoals().stream()
                .map(g -> String.format("- %s: %s (Priority: %s)", 
                        g.getType(), g.getDescription(), g.getPriority()))
                .reduce("", (a, b) -> a + "\n" + b);
        
        String constraintsStr = analyzedModel.getConstraints().stream()
                .map(c -> String.format("- %s: %s", c.getType(), c.getDescription()))
                .reduce("", (a, b) -> a + "\n" + b);

        String template = promptLoader.load("planner_default_planning_v1.md");

        return promptRenderer.render(template, Map.of(
                "GOALS", goalsStr,
                "CONSTRAINTS", constraintsStr
                ));
        /*return String.format("""
                You are a UI planning specialist. Create a detailed execution plan for building a website.
                
                Goals:
                %s
                
                Constraints:
                %s
                
                Create an ordered list of implementation steps. Each step should have:
                - type: one of [CREATE_COMPONENT, APPLY_LAYOUT, APPLY_STYLING, ESTABLISH_HIERARCHY, VALIDATE_CONSTRAINT]
                - description: what needs to be done
                
                Example format:
                [
                  {
                    "type": "CREATE_COMPONENT",
                    "description": "Create header section with name and title"
                  },
                  {
                    "type": "APPLY_LAYOUT",
                    "description": "Apply flexbox layout for responsive design"
                  },
                  {
                    "type": "CREATE_COMPONENT",
                    "description": "Create education section with timeline layout"
                  }
                ]
                
                Return ONLY the JSON array, no additional text.
                """, goalsStr, constraintsStr);*/
    }

    /**
     * Deserializează răspunsul LLM (un obiect JSON cu lista de pagini și pași) în obiecte
     * {@link UIPage}. În caz de eroare la parsare, returnează o pagină implicită de rezervă.
     *
     * @param llmResponse răspunsul brut returnat de LLM
     * @return lista paginilor parsate sau o pagină implicită dacă parsarea eșuează
     */
    private List<UIPage> parsePagesFromLLMResponse(String llmResponse) {
        try {
            String jsonStr = extractJson(llmResponse);
            JsonNode root = objectMapper.readTree(jsonStr);
            JsonNode pagesNode = root.path("pages");

            List<UIPage> pages = new ArrayList<>();

            for (JsonNode pageNode : pagesNode) {
                List<PlanStep> steps = new ArrayList<>();
                int order = 0;

                for (JsonNode stepNode : pageNode.path("steps")) {
                    PlanStep.StepType stepType;
                    try {
                        stepType = PlanStep.StepType.valueOf(
                                stepNode.path("type").asText("CREATE_COMPONENT")
                        );
                    } catch (IllegalArgumentException e) {
                        stepType = PlanStep.StepType.CREATE_COMPONENT;
                    }

                    steps.add(PlanStep.builder()
                            .stepId(UUID.randomUUID().toString())
                            .order(order++)
                            .type(stepType)
                            .description(stepNode.path("description").asText("Unknown step"))
                            .parameters(Map.of())
                            .build());
                }

                pages.add(UIPage.builder()
                        .name(pageNode.path("name").asText("Page"))
                        .route(pageNode.path("route").asText("/"))
                        .steps(steps)
                        .build());
            }

            log.info("Parsed {} pages from LLM response", pages.size());
            return pages;

        } catch (Exception e) {
            log.error("Failed to parse pages from LLM response", e);
            return List.of(UIPage.builder()
                    .name("Home")
                    .route("/")
                    .steps(List.of(PlanStep.builder()
                            .stepId(UUID.randomUUID().toString())
                            .order(0)
                            .type(PlanStep.StepType.CREATE_COMPONENT)
                            .description("Create basic UI structure")
                            .parameters(Map.of())
                            .build()))
                    .build());
        }
    }
    
    /**
     * Curăță răspunsul LLM de eventualele blocuri de cod Markdown (```json ... ```),
     * lăsând doar conținutul JSON.
     *
     * @param response răspunsul brut returnat de LLM
     * @return șirul JSON curățat de delimitatorii Markdown
     */
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
