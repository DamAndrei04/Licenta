package com.uibuilder.mas.util;

import com.uibuilder.mas.agent.builder.model.UIComponentNode;
import com.uibuilder.mas.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.descriptor.SchemaPropertyHandler;
import com.uibuilder.mas.descriptor.UIDescriptor;
import com.uibuilder.mas.descriptor.UIDescriptor.ComponentDescriptor;
import com.uibuilder.mas.descriptor.UIDescriptor.PageDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * Transforms internal UIComponentTree to the ui-descriptor-v1.json schema format.
 * Schema-driven - uses SchemaPropertyHandler for validation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaTransformer {

    private final SchemaPropertyHandler schemaHandler;
    
    /**
     * Transform UIComponentTree to UIDescriptor matching ui-descriptor-v1.json schema.
     */
    public UIDescriptor transform(UIComponentTree tree) {
        log.info("Transforming UIComponentTree to ui-descriptor-v1.json schema");
        
        UIDescriptor descriptor = new UIDescriptor();
        descriptor.setVersion("1.0");
        descriptor.setExportedAt(Instant.now());
        descriptor.setActivePageId("page-home");
        
        // Create single page with all components
        Map<String, PageDescriptor> pages = new HashMap<>();
        PageDescriptor page = transformToPage(tree);
        pages.put("page-home", page);
        
        descriptor.setPages(pages);
        
        log.info("Transformed tree with {} components into schema-compliant descriptor", 
                page.getDroppedItems().size());
        
        return descriptor;
    }
    
    private PageDescriptor transformToPage(UIComponentTree tree) {
        PageDescriptor page = new PageDescriptor();
        page.setName("Home");
        page.setRoute("/");
        page.setSelectedId(null);
        
        // Flatten the tree into droppedItems map
        Map<String, ComponentDescriptor> droppedItems = new LinkedHashMap<>();
        List<String> rootIds = new ArrayList<>();
        
        for (UIComponentNode rootNode : tree.getRootNodes()) {
            flattenNode(rootNode, droppedItems, rootIds, null);
        }
        
        page.setDroppedItems(droppedItems);
        page.setRootIds(rootIds);
        
        return page;
    }
    
    private void flattenNode(UIComponentNode node, 
                            Map<String, ComponentDescriptor> droppedItems,
                            List<String> rootIds,
                            String parentId) {
        
        // Map component type to allowed schema types
        String mappedType = mapToAllowedType(node.getComponentType());
        
        ComponentDescriptor component = new ComponentDescriptor();
        component.setId(node.getNodeId());
        component.setType(mappedType);
        component.setParentId(parentId);
        
        // Extract and validate layout - using Map now
        Map<String, Object> layout = new HashMap<>();
        if (node.getLayout() != null) {
            layout = schemaHandler.filterValidLayoutProps(node.getLayout());
        }
        // Ensure required layout fields exist
        layout.putIfAbsent("x", 0.0);
        layout.putIfAbsent("y", 0.0);
        layout.putIfAbsent("width", 200.0);
        layout.putIfAbsent("height", 40.0);
        component.setLayout(layout);
        
        // Extract and validate props - schema-driven filtering
        Map<String, Object> props = new HashMap<>();
        if (node.getProperties() != null) {
            props = schemaHandler.filterValidProps(node.getProperties());
        }
        component.setProps(props);
        
        // Handle children
        List<String> childrenIds = new ArrayList<>();
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (UIComponentNode child : node.getChildren()) {
                childrenIds.add(child.getNodeId());
                flattenNode(child, droppedItems, rootIds, node.getNodeId());
            }
        }
        component.setChildrenIds(childrenIds);
        
        // Add to droppedItems
        droppedItems.put(node.getNodeId(), component);
        
        // If root level, add to rootIds
        if (parentId == null) {
            rootIds.add(node.getNodeId());
        }
    }
    
    /**
     * Map internal component types to schema-allowed types.
     */
    private String mapToAllowedType(String internalType) {
        if (internalType == null) {
            return "card";
        }
        
        String lower = internalType.toLowerCase();
        
        // Check if directly allowed
        if (schemaHandler.isValidComponentType(lower)) {
            return lower;
        }
        
        // Mappings for common types
        return switch (lower) {
            case "text", "label", "heading", "title", "subtitle" -> "label";
            case "container", "section", "div", "header", "footer", "nav" -> "card";
            case "textfield", "textinput", "email-input", "name-input" -> "input";
            case "cta", "submit", "submit-button" -> "button";
            case "timeline", "timeline-item", "grid", "progress-bar", "form" -> "card";
            default -> "card"; // Default fallback
        };
    }
}
