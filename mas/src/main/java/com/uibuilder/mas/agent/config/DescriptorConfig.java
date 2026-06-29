package com.uibuilder.mas.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurație pentru încărcarea și parsarea descriptorilor. Definește bean-ul
 * {@link ObjectMapper} folosit în tot modulul.
 */
@Configuration
public class DescriptorConfig {

    /**
     * Definește bean-ul {@link ObjectMapper}, configurat cu suport pentru tipurile de
     * dată/oră din Java (modulul JavaTime).
     *
     * @return instanța {@link ObjectMapper} configurată
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
