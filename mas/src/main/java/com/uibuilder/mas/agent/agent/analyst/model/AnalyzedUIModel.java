package com.uibuilder.mas.agent.agent.analyst.model;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Immutable semantic model produced by AnalystAgent.
 * Represents the analyzed understanding of the UI structure.
 */
@Value
@Builder
public class AnalyzedUIModel {
    String analysisId;
    Instant timestamp;
    UIDescriptor sourceDescriptor;
    List<Goal> goals;
    List<Constraint> constraints;
    List<Conflict> conflicts;
}
