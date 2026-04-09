package com.uibuilder.mas.agent.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class PromptLoader {

    private static final String BASE_PATH = "prompts/";

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