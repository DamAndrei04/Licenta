package com.uibuilder.mas.agent.agent.validator;

import lombok.Value;

import java.util.List;

/**
 * Immutable validation result.
 */
@Value
public class ValidationResult {
    boolean valid;
    List<String> violations;
}
