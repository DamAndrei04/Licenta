package com.uibuilder.mas.memory;

import com.uibuilder.mas.agent.validator.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Memory segment for storing validation results.
 */
@Slf4j
public class ValidationMemory {
    
    private final List<ValidationResult> validations = new ArrayList<>();
    
    public void storeValidation(ValidationResult result) {
        log.debug("Storing validation result: valid={}", result.isValid());
        validations.add(result);
    }
    
    public Optional<ValidationResult> getLatestValidation() {
        if (validations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(validations.get(validations.size() - 1));
    }
    
    public List<ValidationResult> getAllValidations() {
        return new ArrayList<>(validations);
    }
    
    public void clear() {
        validations.clear();
    }
}
