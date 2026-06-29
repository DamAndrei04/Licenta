package com.uibuilder.mas.agent.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Încarcă descriptori UI din surse JSON (fișier, flux de intrare sau șir). Nu conține
 * logică de afaceri — realizează doar conversia JSON → obiect.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UIDescriptorLoader {

    private final ObjectMapper objectMapper;

    /**
     * Construiește încărcătorul cu un {@link ObjectMapper} configurat să suporte tipurile
     * de dată/oră din Java (modulul JavaTime).
     */
    public UIDescriptorLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Încarcă un descriptor UI dintr-un fișier indicat prin cale.
     *
     * @param filePath calea fișierului JSON
     * @return descriptorul UI deserializat
     * @throws IOException dacă fișierul nu poate fi citit sau deserializat
     */
    public UIDescriptor loadFromFile(String filePath) throws IOException {
        log.info("Loading UI descriptor from file: {}", filePath);
        return objectMapper.readValue(new File(filePath), UIDescriptor.class);
    }

    /**
     * Încarcă un descriptor UI dintr-un flux de intrare.
     *
     * @param inputStream fluxul de intrare cu conținut JSON
     * @return descriptorul UI deserializat
     * @throws IOException dacă fluxul nu poate fi citit sau deserializat
     */
    public UIDescriptor loadFromStream(InputStream inputStream) throws IOException {
        log.info("Loading UI descriptor from input stream");
        return objectMapper.readValue(inputStream, UIDescriptor.class);
    }

    /**
     * Încarcă un descriptor UI dintr-un șir JSON.
     *
     * @param json șirul JSON de deserializat
     * @return descriptorul UI deserializat
     * @throws IOException dacă șirul nu poate fi deserializat
     */
    public UIDescriptor loadFromJson(String json) throws IOException {
        log.info("Loading UI descriptor from JSON string");
        return objectMapper.readValue(json, UIDescriptor.class);
    }
}
