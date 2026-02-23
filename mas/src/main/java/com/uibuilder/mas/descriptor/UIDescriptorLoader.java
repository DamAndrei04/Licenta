package com.uibuilder.mas.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads UI descriptors from JSON files.
 * No business logic - pure JSON-to-POJO conversion.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UIDescriptorLoader {
    
    private final ObjectMapper objectMapper;
    
    public UIDescriptorLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Load descriptor from file path.
     */
    public UIDescriptor loadFromFile(String filePath) throws IOException {
        log.info("Loading UI descriptor from file: {}", filePath);
        return objectMapper.readValue(new File(filePath), UIDescriptor.class);
    }
    
    /**
     * Load descriptor from input stream.
     */
    public UIDescriptor loadFromStream(InputStream inputStream) throws IOException {
        log.info("Loading UI descriptor from input stream");
        return objectMapper.readValue(inputStream, UIDescriptor.class);
    }
    
    /**
     * Load descriptor from JSON string.
     */
    public UIDescriptor loadFromJson(String json) throws IOException {
        log.info("Loading UI descriptor from JSON string");
        return objectMapper.readValue(json, UIDescriptor.class);
    }
}
