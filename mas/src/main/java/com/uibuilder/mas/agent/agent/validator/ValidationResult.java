package com.uibuilder.mas.agent.agent.validator;

import lombok.Value;

import java.util.List;

/**
 * Rezultatul (imutabil) al validării: indicatorul de validitate și lista încălcărilor găsite.
 */
@Value
public class ValidationResult {
    boolean valid;
    List<String> violations;
}
