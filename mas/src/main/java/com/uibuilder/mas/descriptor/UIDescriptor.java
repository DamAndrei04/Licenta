package com.uibuilder.mas.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Root descriptor matching the exported UI Builder JSON format.
 * Schema-driven - uses Maps for flexible property handling.
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
    
    @Data
    public static class PageDescriptor {
        private String name;
        private String route;
        private Map<String, ComponentDescriptor> droppedItems;
        private List<String> rootIds;
        private String selectedId;
    }
    
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
