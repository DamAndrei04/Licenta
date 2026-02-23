package com.uibuilder.mas.agent.builder.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Immutable tree structure representing built UI components.
 */
@Value
@Builder
public class UIComponentTree {
    String treeId;
    String planId;
    Instant builtAt;
    List<UIComponentNode> rootNodes;
    
    /**
     * Get all nodes in the tree (flattened).
     */
    public List<UIComponentNode> getAllNodes() {
        return rootNodes.stream()
                .flatMap(node -> node.flattenHierarchy().stream())
                .toList();
    }
}
