package com.uibuilder.mas.agent.agent.builder;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;

/**
 * Builder Agent interface - constructs UI components from plans.
 * Does not generate UI directly - delegates to generators.
 */
public interface BuilderAgent {
    
    /**
     * Build UI component tree from execution plan.
     * 
     * @param plan The execution plan
     * @return Immutable component tree
     */
    UIComponentTree build(UIPlan plan);
    
    /**
     * Get agent identifier.
     */
    String getAgentId();
}
