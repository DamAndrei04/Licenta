package com.uibuilder.mas.agent.agent.analyst.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a conflict between goals and/or constraints.
 * Immutable record.
 */
@Value
@Builder
public class Conflict {
    String id;
    String description;
    ConflictSeverity severity;
    List<String> involvedGoalIds;
    List<String> involvedConstraintIds;
    boolean resolved;
    String resolutionStrategy;
}
