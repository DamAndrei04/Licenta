package com.uibuilder.mas.agent.agent.analyst.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a high-level goal extracted from the UI.
 * Immutable record.
 */
@Value
@Builder
public class Goal {
    String id;
    GoalType type;
    String description;
    GoalPriority priority;
    GoalSource source;
    String contextPageId;
    String contextComponentId;
}
