package com.uibuilder.mas.agent.prompt;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic unit tests for {@link PromptRenderer}.
 * Pure logic (Mustache-style placeholder interpolation), so no mocks are needed.
 */
class PromptRendererTest {

    private final PromptRenderer renderer = new PromptRenderer();

    @Test
    void render_replacesSinglePlaceholder() {
        String result = renderer.render("Hello {{NAME}}!", Map.of("NAME", "World"));

        assertEquals("Hello World!", result);
    }

    @Test
    void render_replacesMultiplePlaceholders() {
        Map<String, String> vars = new LinkedHashMap<>();
        vars.put("GREETING", "Hi");
        vars.put("NAME", "Andrei");

        String result = renderer.render("{{GREETING}}, {{NAME}}", vars);

        assertEquals("Hi, Andrei", result);
    }

    @Test
    void render_leavesUnknownPlaceholderUntouched() {
        String result = renderer.render("Value: {{UNKNOWN}}", Map.of("NAME", "x"));

        assertEquals("Value: {{UNKNOWN}}", result);
    }

    @Test
    void render_withNoVariables_returnsTemplateUnchanged() {
        String result = renderer.render("No placeholders here", Map.of());

        assertEquals("No placeholders here", result);
    }
}
