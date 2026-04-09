package com.uibuilder.mas.agent.agent.validator;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;

/**
 * Validator Agent interface - validates built UI components.
 * Does not implement validation logic - delegates to rules.
 */
public interface ValidatorAgent {
    
    /**
     * Validate a built UI component tree.
     * 
     * @param componentTree The component tree to validate
     * @return Validation result
     */
    ValidationResult validate(UIComponentTree componentTree);
    
    /**
     * Get agent identifier.
     */
    String getAgentId();
}
