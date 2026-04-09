package com.uibuilder.mas.agent.agent.planner.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Represents a single step in the execution plan.
 * Immutable record.
 */
@Value
@Builder
public class PlanStep {
    String stepId;
    int order;
    StepType type;
    String description;
    //List<String> dependencies;
    Map<String, Object> parameters;
    
    public enum StepType {
        CREATE_COMPONENT,
        APPLY_LAYOUT,
        APPLY_STYLING,
        ESTABLISH_HIERARCHY,
        VALIDATE_CONSTRAINT,
    }
}
