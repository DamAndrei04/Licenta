package com.uibuilder.mas.agent.agent.builder.model;

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
    List<UIBuiltPage> pages;
    
    /**
     * Get all nodes in the tree (flattened).
     */
    public List<UIComponentNode> getAllNodes() {
        return pages.stream()
                .flatMap(page -> page.getComponents().stream())
                .flatMap(node -> node.flattenHierarchy().stream())
                .toList();
    }
}
