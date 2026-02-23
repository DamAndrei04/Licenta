package com.uibuilder.mas.descriptor;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a high-level design goal extracted from the UI descriptor.
 * Immutable semantic representation.
 */
@Value
@Builder
public class UIDesignGoal {
    String id;
    String description;
    String targetPageId;
    GoalCategory category;
    
    public enum GoalCategory {
        LAYOUT,
        INTERACTION,
        ACCESSIBILITY,
        RESPONSIVENESS,
        VISUAL_HIERARCHY
    }
}
