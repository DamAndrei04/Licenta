package com.uibuilder.mas.agent.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Metode utilitare pentru serializarea și deserializarea JSON, construite peste un
 * {@link ObjectMapper}. Erorile sunt tratate intern (jurnalizate), returnând valori de
 * rezervă în loc să propage excepții.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    /**
     * Serializează un obiect într-un șir JSON.
     *
     * @param object obiectul care trebuie serializat
     * @return reprezentarea JSON a obiectului sau {@code "{}"} în caz de eroare
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            return "{}";
        }
    }

    /**
     * Deserializează un șir JSON într-un obiect de tipul indicat.
     *
     * @param json șirul JSON care trebuie deserializat
     * @param clazz clasa tipului țintă
     * @param <T> tipul obiectului rezultat
     * @return obiectul deserializat sau {@code null} în caz de eroare
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}", clazz.getName(), e);
            return null;
        }
    }
}
