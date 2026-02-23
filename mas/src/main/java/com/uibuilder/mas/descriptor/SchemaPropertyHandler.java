package com.uibuilder.mas.descriptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Schema-driven property handler that loads allowed properties from ui-descriptor-v1.json.
 * No hardcoding - everything driven by the JSON schema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaPropertyHandler {
    
    private final ObjectMapper objectMapper;
    
    private Set<String> allowedComponentTypes;
    private Set<String> allowedComponentProps;
    private Set<String> allowedStyleProps;
    private Set<String> allowedLayoutProps;
    private Map<String, List<String>> enumValues;
    
    @PostConstruct
    public void init() {
        try {
            loadSchemaDefinitions();
            log.info("Loaded schema definitions: {} component types, {} props, {} style props, {} layout props",
                    allowedComponentTypes.size(), allowedComponentProps.size(), 
                    allowedStyleProps.size(), allowedLayoutProps.size());
        } catch (Exception e) {
            log.error("Failed to load schema definitions", e);
            initializeDefaults();
        }
    }
    
    private void loadSchemaDefinitions() throws IOException {
        ClassPathResource resource = new ClassPathResource("schema/ui-descriptor-v1.json");
        try (InputStream is = resource.getInputStream()) {
            JsonNode schema = objectMapper.readTree(is);
            JsonNode defs = schema.path("$defs");
            
            // Extract allowed component types
            allowedComponentTypes = extractEnumValues(defs.path("component").path("properties").path("type"));
            
            // Extract allowed component props
            allowedComponentProps = extractPropertyNames(defs.path("componentProps").path("properties"));
            
            // Extract allowed style props
            allowedStyleProps = extractPropertyNames(defs.path("styleObject").path("properties"));
            
            // Extract allowed layout props
            allowedLayoutProps = extractPropertyNames(defs.path("layout").path("properties"));
            
            // Extract enum values for validation
            enumValues = new HashMap<>();
            extractEnumValuesForProps(defs.path("componentProps").path("properties"));
            extractEnumValuesForProps(defs.path("styleObject").path("properties"));
        }
    }
    
    private Set<String> extractEnumValues(JsonNode typeNode) {
        Set<String> values = new HashSet<>();
        JsonNode enumNode = typeNode.path("enum");
        if (enumNode.isArray()) {
            enumNode.forEach(node -> values.add(node.asText()));
        }
        return values;
    }
    
    private Set<String> extractPropertyNames(JsonNode propsNode) {
        Set<String> names = new HashSet<>();
        propsNode.fieldNames().forEachRemaining(names::add);
        return names;
    }
    
    private void extractEnumValuesForProps(JsonNode propsNode) {
        propsNode.fields().forEachRemaining(entry -> {
            String propName = entry.getKey();
            JsonNode propDef = entry.getValue();
            JsonNode enumNode = propDef.path("enum");
            if (enumNode.isArray()) {
                List<String> values = new ArrayList<>();
                enumNode.forEach(node -> values.add(node.asText()));
                enumValues.put(propName, values);
            }
        });
    }
    
    private void initializeDefaults() {
        // Fallback defaults if schema loading fails
        allowedComponentTypes = Set.of("button", "input", "card", "label", "accordion");
        allowedComponentProps = Set.of("text", "children", "placeholder", "value", "type", 
                                      "variant", "size", "className", "style", "disabled");
        allowedStyleProps = Set.of("backgroundColor", "backgroundImage", "color", "borderColor",
                                  "borderWidth", "borderStyle", "borderRadius", "border",
                                  "padding", "margin", "marginBottom", "fontFamily", "fontSize",
                                  "fontWeight", "fontStyle", "textDecoration", "textAlign",
                                  "lineHeight", "letterSpacing", "opacity", "boxShadow",
                                  "display", "alignItems", "justifyContent", "position", "cursor");
        allowedLayoutProps = Set.of("x", "y", "width", "height");
        enumValues = new HashMap<>();
    }
    
    // Validation methods
    
    public boolean isValidComponentType(String type) {
        return allowedComponentTypes.contains(type);
    }
    
    public boolean isValidProp(String propName) {
        return allowedComponentProps.contains(propName);
    }
    
    public boolean isValidStyleProp(String propName) {
        return allowedStyleProps.contains(propName);
    }
    
    public boolean isValidLayoutProp(String propName) {
        return allowedLayoutProps.contains(propName);
    }
    
    public Map<String, Object> filterValidProps(Map<String, Object> props) {
        Map<String, Object> filtered = new HashMap<>();
        
        props.forEach((key, value) -> {
            if (isValidProp(key)) {
                // Special handling for style object
                if ("style".equals(key) && value instanceof Map) {
                    filtered.put(key, filterValidStyleProps((Map<String, Object>) value));
                } else {
                    filtered.put(key, value);
                }
            } else {
                log.debug("Filtered out invalid prop: {}", key);
            }
        });
        
        return filtered;
    }
    
    public Map<String, Object> filterValidStyleProps(Map<String, Object> style) {
        Map<String, Object> filtered = new HashMap<>();
        
        style.forEach((key, value) -> {
            if (isValidStyleProp(key)) {
                filtered.put(key, value);
            } else {
                log.debug("Filtered out invalid style prop: {}", key);
            }
        });
        
        return filtered;
    }
    
    public Map<String, Object> filterValidLayoutProps(Map<String, Object> layout) {
        Map<String, Object> filtered = new HashMap<>();
        
        layout.forEach((key, value) -> {
            if (isValidLayoutProp(key)) {
                filtered.put(key, value);
            } else {
                log.debug("Filtered out invalid layout prop: {}", key);
            }
        });
        
        return filtered;
    }
    
    // Getters for schema info
    
    public Set<String> getAllowedComponentTypes() {
        return Collections.unmodifiableSet(allowedComponentTypes);
    }
    
    public Set<String> getAllowedComponentProps() {
        return Collections.unmodifiableSet(allowedComponentProps);
    }
    
    public Set<String> getAllowedStyleProps() {
        return Collections.unmodifiableSet(allowedStyleProps);
    }
    
    public Set<String> getAllowedLayoutProps() {
        return Collections.unmodifiableSet(allowedLayoutProps);
    }
}
