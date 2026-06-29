package com.uibuilder.mas.agent.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Descriptorul rădăcină care corespunde formatului JSON exportat de constructorul de
 * interfețe. Bazat pe schemă — folosește hărți pentru gestionarea flexibilă a
 * proprietăților. Conține versiunea, pagina activă și paginile interfeței.
 */
@Data
public class UIDescriptor {
    
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("exportedAt")
    private Instant exportedAt;
    
    @JsonProperty("activePageId")
    private String activePageId;
    
    @JsonProperty("pages")
    private Map<String, PageDescriptor> pages;
    
    /**
     * Descriptorul unei pagini: nume, rută, componentele plasate, identificatorii
     * componentelor rădăcină și componenta selectată.
     */
    @Data
    public static class PageDescriptor {
        private String name;
        private String route;
        private Map<String, ComponentDescriptor> droppedItems;
        private List<String> rootIds;
        private String selectedId;
    }

    /**
     * Descriptorul unei componente: id, tip, layout, părinte, copii, proprietăți și aliniere.
     */
    @Data
    public static class ComponentDescriptor {
        private String id;
        private String type;
        private Map<String, Object> layout;  // Changed to Map for flexibility
        private String parentId;
        private List<String> childrenIds;
        private Map<String, Object> props;   // Changed to Map - schema-driven validation
        private String alignment;
    }
}
