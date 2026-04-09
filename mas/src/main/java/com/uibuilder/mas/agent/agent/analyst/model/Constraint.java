package com.uibuilder.mas.agent.agent.analyst.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a constraint on the UI design.
 * Immutable record.
 */
@Value
@Builder
public class Constraint {
    String id;
    String type;
    String description;
    String targetComponentId;
    Object constraintValue;
    boolean satisfied;
}
