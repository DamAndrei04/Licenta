package com.uibuilder.mas.agent.validator.rule;

import com.uibuilder.mas.agent.builder.model.UIComponentTree;

import java.util.List;

/**
 * Interface for validation rules.
 * Each rule performs a specific validation check.
 */
public interface ValidationRule {
    
    /**
     * Validate component tree and return violations.
     * 
     * @param componentTree Tree to validate
     * @return List of violation messages (empty if valid)
     */
    List<String> validate(UIComponentTree componentTree);
    
    /**
     * Get rule name/identifier.
     */
    String getRuleName();
}
