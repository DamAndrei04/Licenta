package com.uibuilder.mas.agent.descriptor;

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
 * Gestionar de proprietăți bazat pe schemă, care încarcă tipurile și proprietățile permise
 * din fișierul {@code ui-descriptor-v1.json}. Nimic nu este codat rigid: totul este derivat
 * din schema JSON. Oferă metode de validare și de filtrare a proprietăților, stilurilor și
 * layout-ului componentelor.
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

    /**
     * Inițializează gestionarul după construirea bean-ului, încărcând definițiile din schemă.
     * Dacă încărcarea eșuează, recurge la un set implicit de valori.
     */
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
    
    /**
     * Încarcă din fișierul de schemă tipurile de componente, proprietățile, stilurile,
     * proprietățile de layout permise și valorile de enumerare folosite la validare.
     *
     * @throws IOException dacă fișierul de schemă nu poate fi citit
     */
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
    
    /**
     * Extrage valorile permise dintr-un nod de schemă care conține o listă {@code enum}.
     *
     * @param typeNode nodul de schemă care conține definiția {@code enum}
     * @return mulțimea valorilor permise
     */
    private Set<String> extractEnumValues(JsonNode typeNode) {
        Set<String> values = new HashSet<>();
        JsonNode enumNode = typeNode.path("enum");
        if (enumNode.isArray()) {
            enumNode.forEach(node -> values.add(node.asText()));
        }
        return values;
    }
    
    /**
     * Extrage numele proprietăților dintr-un nod {@code properties} al schemei.
     *
     * @param propsNode nodul {@code properties} din schemă
     * @return mulțimea numelor de proprietăți
     */
    private Set<String> extractPropertyNames(JsonNode propsNode) {
        Set<String> names = new HashSet<>();
        propsNode.fieldNames().forEachRemaining(names::add);
        return names;
    }
    
    /**
     * Parcurge proprietățile dintr-un nod de schemă și reține, pentru cele cu valori
     * {@code enum}, lista valorilor permise în harta {@code enumValues}.
     *
     * @param propsNode nodul {@code properties} din schemă
     */
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
    
    /**
     * Inițializează un set implicit de tipuri, proprietăți, stiluri și proprietăți de layout
     * permise, folosit ca rezervă atunci când încărcarea schemei eșuează.
     */
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
    
    /**
     * Verifică dacă un tip de componentă este permis de schemă.
     *
     * @param type tipul de componentă verificat
     * @return {@code true} dacă tipul este permis, altfel {@code false}
     */
    public boolean isValidComponentType(String type) {
        return allowedComponentTypes.contains(type);
    }

    /**
     * Verifică dacă o proprietate de componentă este permisă de schemă.
     *
     * @param propName numele proprietății verificate
     * @return {@code true} dacă proprietatea este permisă, altfel {@code false}
     */
    public boolean isValidProp(String propName) {
        return allowedComponentProps.contains(propName);
    }

    /**
     * Verifică dacă o proprietate de stil este permisă de schemă.
     *
     * @param propName numele proprietății de stil verificate
     * @return {@code true} dacă proprietatea de stil este permisă, altfel {@code false}
     */
    public boolean isValidStyleProp(String propName) {
        return allowedStyleProps.contains(propName);
    }

    /**
     * Verifică dacă o proprietate de layout este permisă de schemă.
     *
     * @param propName numele proprietății de layout verificate
     * @return {@code true} dacă proprietatea de layout este permisă, altfel {@code false}
     */
    public boolean isValidLayoutProp(String propName) {
        return allowedLayoutProps.contains(propName);
    }

    /**
     * Filtrează o hartă de proprietăți, păstrând doar proprietățile permise de schemă;
     * pentru obiectul {@code style} aplică recursiv filtrarea proprietăților de stil.
     *
     * @param props harta de proprietăți de filtrat
     * @return o hartă nouă ce conține doar proprietățile valide
     */
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
    
    /**
     * Filtrează o hartă de proprietăți de stil, păstrând doar pe cele permise de schemă.
     *
     * @param style harta de proprietăți de stil de filtrat
     * @return o hartă nouă ce conține doar proprietățile de stil valide
     */
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
    
    /**
     * Filtrează o hartă de proprietăți de layout, păstrând doar pe cele permise de schemă.
     *
     * @param layout harta de proprietăți de layout de filtrat
     * @return o hartă nouă ce conține doar proprietățile de layout valide
     */
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
    
    /**
     * Returnează mulțimea (needitabilă) a tipurilor de componente permise.
     *
     * @return tipurile de componente permise
     */
    public Set<String> getAllowedComponentTypes() {
        return Collections.unmodifiableSet(allowedComponentTypes);
    }

    /**
     * Returnează mulțimea (needitabilă) a proprietăților de componentă permise.
     *
     * @return proprietățile de componentă permise
     */
    public Set<String> getAllowedComponentProps() {
        return Collections.unmodifiableSet(allowedComponentProps);
    }

    /**
     * Returnează mulțimea (needitabilă) a proprietăților de stil permise.
     *
     * @return proprietățile de stil permise
     */
    public Set<String> getAllowedStyleProps() {
        return Collections.unmodifiableSet(allowedStyleProps);
    }

    /**
     * Returnează mulțimea (needitabilă) a proprietăților de layout permise.
     *
     * @return proprietățile de layout permise
     */
    public Set<String> getAllowedLayoutProps() {
        return Collections.unmodifiableSet(allowedLayoutProps);
    }
}
