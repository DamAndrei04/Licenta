package com.uibuilder.mas.agent.analyst;

import com.uibuilder.mas.agent.analyst.model.AnalyzedUIModel;

/**
 * Analyst Agent interface - analyzes user requirements and extracts semantic models.
 * Delegates reasoning to extractors powered by LLM.
 */
public interface AnalystAgent {
    
    /**
     * Analyze a user requirement and produce a semantic model.
     * 
     * @param userRequirement The user's request (e.g., "Make a CV Website...")
     * @return Immutable analyzed model
     */
    AnalyzedUIModel analyze(String userRequirement);
    
    /**
     * Get agent identifier.
     */
    String getAgentId();
}
