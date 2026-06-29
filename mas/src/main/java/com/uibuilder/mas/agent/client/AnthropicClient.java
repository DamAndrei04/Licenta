package com.uibuilder.mas.agent.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client pentru comunicarea cu API-ul Claude de la Anthropic. Construiește cererile HTTP,
 * trimite prompt-urile către model și extrage textul răspunsului. Cheia de API, URL-ul,
 * modelul și numărul maxim de token-uri sunt configurabile.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnthropicClient {
    
    private final ObjectMapper objectMapper;
    
    @Value("${anthropic.api.key:${ANTHROPIC_API_KEY:}}")
    private String apiKey;
    
    @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;
    
    @Value("${anthropic.model:claude-haiku-4-5-20251001}")
    private String model;
    
    @Value("${anthropic.max.tokens:64000}")
    private int maxTokens;
    
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    
    /**
     * Trimite un prompt către modelul Claude și returnează textul răspunsului.
     *
     * @param prompt prompt-ul care trebuie trimis modelului
     * @return textul răspunsului generat de Claude
     * @throws IllegalStateException dacă cheia de API nu este configurată
     * @throws RuntimeException dacă apelul către API eșuează sau întoarce o eroare
     */
    public String sendMessage(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Anthropic API key not configured. Set ANTHROPIC_API_KEY environment variable.");
            throw new IllegalStateException("Anthropic API key not configured");
        }
        
        try {
            log.debug("Sending prompt to Anthropic API (length: {})", prompt.length());
            
            String requestBody = buildRequestBody(prompt);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .timeout(Duration.ofMinutes(5))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Anthropic API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Anthropic API error: " + response.statusCode());
            }
            
            String textResponse = extractTextFromResponse(response.body());
            log.debug("Received response from Anthropic API (length: {})", textResponse.length());
            
            return textResponse;
            
        } catch (Exception e) {
            log.error("Error calling Anthropic API", e);
            throw new RuntimeException("Failed to call Anthropic API", e);
        }
    }
    
    /**
     * Construiește corpul JSON al cererii către API, incluzând modelul, numărul maxim de
     * token-uri și prompt-ul (cu caractere speciale escapate).
     *
     * @param prompt prompt-ul care va fi inclus în cerere
     * @return corpul JSON al cererii
     * @throws Exception dacă apare o eroare la construirea corpului cererii
     */
    private String buildRequestBody(String prompt) throws Exception {
        String json = String.format("""
                {
                    "model": "%s",
                    "max_tokens": %d,
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """, model, maxTokens, escapeJson(prompt));
        
        return json;
    }
    
    /**
     * Extrage textul generat din corpul JSON al răspunsului API (primul element din
     * tabloul {@code content}).
     *
     * @param responseBody corpul JSON al răspunsului primit de la API
     * @return textul răspunsului
     * @throws Exception dacă formatul răspunsului este neașteptat
     */
    private String extractTextFromResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode content = root.path("content");
        
        if (content.isArray() && content.size() > 0) {
            return content.get(0).path("text").asText();
        }
        
        throw new RuntimeException("Unexpected response format from Anthropic API");
    }
    
    /**
     * Escapează caracterele speciale dintr-un text pentru a putea fi inclus în siguranță
     * într-un șir JSON (backslash, ghilimele, rând nou, retur de car și tab).
     *
     * @param text textul care trebuie escapat
     * @return textul cu caracterele speciale escapate
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
