package com.uibuilder.mas.agent.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PromptRenderer {

    public String render(String template, Map<String, String> variables) {
        String rendered = template;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            rendered = rendered.replace(placeholder, entry.getValue());
        }

        return rendered;
    }
}
