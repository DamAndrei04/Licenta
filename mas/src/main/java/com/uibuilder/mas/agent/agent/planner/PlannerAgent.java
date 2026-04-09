package com.uibuilder.mas.agent.agent.planner;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;

/**
 * Planner Agent interface - creates execution plans from analyzed models.
 * Does not implement planning logic directly - delegates to strategies.
 */
public interface PlannerAgent {
    
    /**
     * Generate a UI construction plan from analyzed model.
     * 
     * @param analyzedModel The analyzed semantic model
     * @return Immutable execution plan
     */
    UIPlan createPlan(AnalyzedUIModel analyzedModel);
    
    /**
     * Get agent identifier.
     */
    String getAgentId();
}
