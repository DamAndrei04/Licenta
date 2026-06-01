package com.uibuilder.mas.agent.util;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentNode;
import com.uibuilder.mas.agent.agent.builder.model.UIBuiltPage;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.descriptor.SchemaPropertyHandler;
import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import com.uibuilder.mas.agent.descriptor.UIDescriptor.ComponentDescriptor;
import com.uibuilder.mas.agent.descriptor.UIDescriptor.PageDescriptor;
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

        Map<String, PageDescriptor> pages = new LinkedHashMap<>();

        tree.getPages().forEach(builtPage -> {
            String pageId = "page-" + builtPage.getRoute()
                    .replace("/", "")
                    .replace(":", "")
                    .replace("-", "");
            if (pageId.equals("page-")) pageId = "page-home";

            PageDescriptor pageDescriptor = transformToPage(builtPage);
            pages.put(pageId, pageDescriptor);
        });

        // Set first page as active
        descriptor.setActivePageId(pages.keySet().iterator().next());
        descriptor.setPages(pages);

        log.info("Transformed {} pages into schema-compliant descriptor", pages.size());
        return descriptor;
    }

    private PageDescriptor transformToPage(UIBuiltPage builtPage) {
        PageDescriptor page = new PageDescriptor();
        page.setName(builtPage.getName());
        page.setRoute(builtPage.getRoute());
        page.setSelectedId(null);

        Map<String, ComponentDescriptor> droppedItems = new LinkedHashMap<>();
        List<String> rootIds = new ArrayList<>();

        for (UIComponentNode rootNode : builtPage.getComponents()) {
            flattenNode(rootNode, droppedItems, rootIds, null);
        }

        // Fix 1: correct any root sections that overlap each other due to
        // LLM arithmetic errors in cumulative y calculation.
        fixRootSectionOverlaps(droppedItems, rootIds);

        page.setDroppedItems(droppedItems);
        page.setRootIds(rootIds);

        return page;
    }

    /**
     * Fix 1 — Root section overlap correction.
     *
     * Root sections are placed in the LLM's intended array order (top → bottom).
     * For each section, we compute the minimum y it can occupy given all sections
     * already placed. Two sections conflict only when their x-ranges overlap
     * (side-by-side sections at the same y are intentional and left untouched).
     */
    private void fixRootSectionOverlaps(Map<String, ComponentDescriptor> droppedItems,
                                        List<String> rootIds) {
        if (rootIds.size() <= 1) return;

        // [x, y, width, height] of every section placed so far
        List<double[]> placed = new ArrayList<>();

        for (String id : rootIds) {
            Map<String, Object> layout = droppedItems.get(id).getLayout();

            double x = layoutDouble(layout, "x");
            double y = layoutDouble(layout, "y");
            double w = layoutDouble(layout, "width");
            double h = layoutDouble(layout, "height");

            // Minimum y this section must start at to avoid overlapping any
            // already-placed section whose x-range intersects ours.
            double requiredY = 0.0;
            for (double[] p : placed) {
                boolean xOverlap = x < p[0] + p[2] && x + w > p[0];
                if (xOverlap) {
                    requiredY = Math.max(requiredY, p[1] + p[3]);
                }
            }

            double finalY = Math.max(y, requiredY);
            if (finalY != y) {
                log.debug("Layout fix: root section '{}' y {} → {} (overlap correction)", id, y, finalY);
                layout.put("y", finalY);
                y = finalY;
            }

            placed.add(new double[]{x, y, w, h});
        }
    }

    private void flattenNode(UIComponentNode node,
                             Map<String, ComponentDescriptor> droppedItems,
                             List<String> rootIds,
                             String parentId) {

        String mappedType = mapToAllowedType(node.getComponentType());

        ComponentDescriptor component = new ComponentDescriptor();
        component.setId(node.getNodeId());
        component.setType(mappedType);
        component.setParentId(parentId);

        // Extract and validate layout
        Map<String, Object> layout = new HashMap<>();
        if (node.getLayout() != null) {
            layout = schemaHandler.filterValidLayoutProps(node.getLayout());
        }
        layout.putIfAbsent("x", 0.0);
        layout.putIfAbsent("y", 0.0);
        layout.putIfAbsent("width", 200.0);
        layout.putIfAbsent("height", 40.0);
        component.setLayout(layout);

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

            // Fix 2: if any child extends beyond the declared parent height,
            // expand the parent so the child doesn't bleed into the section below.
            double maxChildBottom = 0.0;
            for (UIComponentNode child : node.getChildren()) {
                if (child.getLayout() != null) {
                    double childBottom = layoutDouble(child.getLayout(), "y")
                            + layoutDouble(child.getLayout(), "height");
                    maxChildBottom = Math.max(maxChildBottom, childBottom);
                }
            }
            double declaredHeight = layoutDouble(layout, "height");
            if (maxChildBottom > declaredHeight) {
                double corrected = maxChildBottom + 20; // 20 px breathing room
                log.debug("Layout fix: parent '{}' height {} → {} (child overflow correction)",
                        node.getNodeId(), declaredHeight, corrected);
                layout.put("height", corrected);
            }
        }

        component.setChildrenIds(childrenIds);

        droppedItems.put(node.getNodeId(), component);

        if (parentId == null) {
            rootIds.add(node.getNodeId());
        }
    }

    /** Safely read a numeric value from a layout map as a double. */
    private double layoutDouble(Map<String, Object> layout, String key) {
        if (layout == null) return 0.0;
        Object val = layout.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        return 0.0;
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
