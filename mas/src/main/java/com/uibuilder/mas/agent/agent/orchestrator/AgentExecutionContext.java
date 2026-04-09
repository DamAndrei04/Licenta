package com.uibuilder.mas.agent.agent.orchestrator;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.agent.validator.ValidationResult;
import lombok.Data;

/**
 * Context object holding state across agent executions.
 * Mutable container for orchestration state.
 */
@Data
public class AgentExecutionContext {
    private AnalyzedUIModel analyzedModel;
    private UIPlan plan;
    private UIComponentTree componentTree;
    private ValidationResult validationResult;
}
