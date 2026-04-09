package com.uibuilder.mas.agent.agent.builder.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uibuilder.mas.agent.agent.builder.model.UIBuiltPage;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentNode;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.model.UIPage;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.client.AnthropicClient;
import com.uibuilder.mas.agent.prompt.PromptLoader;
import com.uibuilder.mas.agent.prompt.PromptRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * Generates UI component nodes from plan steps using LLM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComponentGenerator {
    
    private final AnthropicClient anthropicClient;
    private final ObjectMapper objectMapper;
    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;

    public UIComponentTree generate(UIPlan plan) {
        log.debug("Generating pages from plan: {}", plan.getPlanId());

        List<UIBuiltPage> builtPages = new ArrayList<>();
        List<UIPage> pages = plan.getPages();

        for (int i = 0; i < pages.size(); i++) {
            UIPage page = pages.get(i);
            log.info("Generating page {}/{} '{}' ({})", i + 1, pages.size(), page.getName(), page.getRoute());

            String prompt = buildPromptForPage(page);
            String llmResponse = anthropicClient.sendMessage(prompt);
            log.debug("RAW LLM RESPONSE (page: {}):\n{}", page.getName(), llmResponse);

            List<UIComponentNode> pageNodes = parseComponentsFromLLMResponse(llmResponse);

            builtPages.add(UIBuiltPage.builder()
                    .name(page.getName())
                    .route(page.getRoute())
                    .components(pageNodes)
                    .build());

            // Wait between pages to avoid hitting OTPM rate limit
            if (i < pages.size() - 1) {
                try {
                    log.info("Waiting 30s before next page to respect OTPM rate limit...");
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Page generation sleep interrupted");
                }
            }
        }

        return UIComponentTree.builder()
                .treeId(UUID.randomUUID().toString())
                .planId(plan.getPlanId())
                .builtAt(Instant.now())
                .pages(builtPages)
                .build();
    }
    
    private String buildPromptForPage(UIPage page) {
        String stepsStr = page.getSteps().stream()
                .map(s -> String.format("%d. [%s] %s", s.getOrder(), s.getType(), s.getDescription()))
                .reduce("", (a, b) -> a + "\n" + b);

        String template = promptLoader.load("builder_component_generation_v1.md");

        return promptRenderer.render(template, Map.of(
                "PAGE_NAME", page.getName(),
                "PAGE_ROUTE", page.getRoute(),
                "PLAN_STEPS", stepsStr
        ));

        /*return String.format("""
                You are a UI component generator. Generate a complete component tree based on the following plan.
                
                Plan Steps:
                %s
                
                Generate a hierarchical JSON structure representing the UI components. Each component should have:
                - id: unique identifier
                - type: component type (e.g., "container", "header", "section", "card", "button", "input", "text")
                - properties: object with component-specific properties (text, placeholder, variant, etc.)
                - layout: object with positioning (x, y, width, height)
                - children: array of child components (if any)
                
                Example format for a CV website:
                [
                  {
                    "id": "root-container",
                    "type": "container",
                    "properties": {
                      "className": "cv-website",
                      "style": {"backgroundColor": "#f8f9fa"}
                    },
                    "layout": {"x": 0, "y": 0, "width": 1200, "height": 2000},
                    "children": [
                      {
                        "id": "header",
                        "type": "header",
                        "properties": {
                          "text": "John Doe",
                          "subtitle": "Software Engineer"
                        },
                        "layout": {"x": 0, "y": 0, "width": 1200, "height": 200},
                        "children": []
                      },
                      {
                        "id": "education-section",
                        "type": "section",
                        "properties": {"title": "Education"},
                        "layout": {"x": 0, "y": 220, "width": 1200, "height": 300},
                        "children": []
                      }
                    ]
                  }
                ]
                
                Return ONLY the JSON array, no additional text. Make it realistic and complete.
                """, stepsStr);*/
    }
    
    private List<UIComponentNode> parseComponentsFromLLMResponse(String llmResponse) {
        try {
            String jsonStr = extractJson(llmResponse);
            
            JsonNode rootArray = objectMapper.readTree(jsonStr);
            List<UIComponentNode> rootNodes = new ArrayList<>();
            
            for (JsonNode nodeJson : rootArray) {
                UIComponentNode node = parseNode(nodeJson, null);
                if (node != null) {
                    rootNodes.add(node);
                }
            }
            
            log.info("Successfully parsed {} root components from LLM response", rootNodes.size());
            return rootNodes;
            
        } catch (Exception e) {
            log.error("Failed to parse components from LLM response", e);
            // Return fallback component
            return List.of(UIComponentNode.builder()
                    .nodeId(UUID.randomUUID().toString())
                    .componentType("container")
                    .parentNodeId(null)
                    .children(List.of())
                    .properties(Map.of("text", "UI Component"))
                    .layout(Map.of("x", 0, "y", 0, "width", 800, "height", 600))
                    .build());
        }
    }
    
    private UIComponentNode parseNode(JsonNode nodeJson, String parentId) {
        try {
            String nodeId = nodeJson.path("id").asText(UUID.randomUUID().toString());
            String type = nodeJson.path("type").asText("div");
            
            Map<String, Object> properties = new HashMap<>();
            if (nodeJson.has("properties")) {
                nodeJson.path("properties").fields().forEachRemaining(entry -> {
                    properties.put(entry.getKey(), convertJsonNode(entry.getValue()));
                });
            }
            
            Map<String, Object> layout = new HashMap<>();
            if (nodeJson.has("layout")) {
                nodeJson.path("layout").fields().forEachRemaining(entry -> {
                    layout.put(entry.getKey(), convertJsonNode(entry.getValue()));
                });
            }
            
            List<UIComponentNode> children = new ArrayList<>();
            if (nodeJson.has("children") && nodeJson.path("children").isArray()) {
                for (JsonNode childJson : nodeJson.path("children")) {
                    UIComponentNode child = parseNode(childJson, nodeId);
                    if (child != null) {
                        children.add(child);
                    }
                }
            }
            
            return UIComponentNode.builder()
                    .nodeId(nodeId)
                    .componentType(type)
                    .parentNodeId(parentId)
                    .children(children)
                    .properties(properties)
                    .layout(layout)
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to parse component node", e);
            return null;
        }
    }
    
    private Object convertJsonNode(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            if (node.isInt()) {
                return node.asInt();
            } else {
                return node.asDouble();
            }
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            node.fields().forEachRemaining(entry -> {
                map.put(entry.getKey(), convertJsonNode(entry.getValue()));
            });
            return map;
        } else if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(item -> list.add(convertJsonNode(item)));
            return list;
        }
        return node.asText();
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
