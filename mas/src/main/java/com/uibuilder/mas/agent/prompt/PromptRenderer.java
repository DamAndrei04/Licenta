package com.uibuilder.mas.agent.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Componentă care randează șabloanele de prompt, înlocuind marcajele de forma
 * {@code {{CHEIE}}} cu valorile corespunzătoare din harta de variabile.
 */
@Component
public class PromptRenderer {

    /**
     * Randează un șablon prin înlocuirea fiecărui marcaj {@code {{cheie}}} cu valoarea
     * corespunzătoare din harta de variabile.
     *
     * @param template șablonul de prompt care conține marcaje
     * @param variables harta de variabile (cheie → valoare) folosită la înlocuire
     * @return șablonul randat, cu marcajele înlocuite
     */
    public String render(String template, Map<String, String> variables) {
        String rendered = template;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            rendered = rendered.replace(placeholder, entry.getValue());
        }

        return rendered;
    }
}
