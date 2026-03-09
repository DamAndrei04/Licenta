package com.uibuilder.mas.agent.planner.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Immutable execution plan for UI construction.
 */
@Value
@Builder
public class UIPlan {
    String planId;
    String analysisId;
    Instant createdAt;
    List<UIPage> pages;
    int estimatedComplexity;
}
