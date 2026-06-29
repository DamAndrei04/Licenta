package com.uibuilder.mas.agent.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Componentă care încarcă șabloanele de prompt din resursele aplicației (directorul
 * {@code prompts/} de pe classpath).
 */
@Slf4j
@Component
public class PromptLoader {

    private static final String BASE_PATH = "prompts/";

    /**
     * Încarcă conținutul unui șablon de prompt din classpath, din directorul {@code prompts/}.
     *
     * @param relativePath calea relativă a fișierului de șablon (ex. „builder_component_generation_v1.md”)
     * @return conținutul șablonului ca șir de caractere
     * @throws RuntimeException dacă fișierul de șablon nu poate fi încărcat
     */
    public String load(String relativePath) {
        String fullPath = BASE_PATH + relativePath;

        try (InputStream inputStream = new ClassPathResource(fullPath).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load prompt file: {}", fullPath, e);
            throw new RuntimeException("Could not load prompt template: " + fullPath, e);
        }
    }
}