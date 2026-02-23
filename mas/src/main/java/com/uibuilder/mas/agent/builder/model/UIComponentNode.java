package com.uibuilder.mas.agent.builder.model;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a single UI component node in the tree.
 * Immutable record.
 */
@Value
@Builder
public class UIComponentNode {
    String nodeId;
    String componentType;
    String parentNodeId;
    List<UIComponentNode> children;
    Map<String, Object> properties;
    Map<String, Object> layout;
    
    /**
     * Recursively flatten this node and all descendants.
     */
    public List<UIComponentNode> flattenHierarchy() {
        List<UIComponentNode> flattened = new ArrayList<>();
        flattened.add(this);
        
        if (children != null) {
            children.forEach(child -> flattened.addAll(child.flattenHierarchy()));
        }
        
        return flattened;
    }
}
