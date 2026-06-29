package com.uibuilder.mas.agent.memory;

import com.uibuilder.mas.agent.agent.validator.ValidationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Segment de memorie pentru stocarea rezultatelor validării.
 */
@Slf4j
public class ValidationMemory {

    private final List<ValidationResult> validations = new ArrayList<>();

    /**
     * Stochează un rezultat al validării.
     *
     * @param result rezultatul validării care trebuie stocat
     */
    public void storeValidation(ValidationResult result) {
        log.debug("Storing validation result: valid={}", result.isValid());
        validations.add(result);
    }

    /**
     * Returnează cel mai recent rezultat al validării.
     *
     * @return un {@link Optional} cu ultimul rezultat sau gol dacă nu există niciunul
     */
    public Optional<ValidationResult> getLatestValidation() {
        if (validations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(validations.get(validations.size() - 1));
    }

    /**
     * Returnează toate rezultatele validării stocate.
     *
     * @return o copie a listei de rezultate ale validării
     */
    public List<ValidationResult> getAllValidations() {
        return new ArrayList<>(validations);
    }

    /**
     * Golește segmentul de memorie.
     */
    public void clear() {
        validations.clear();
    }
}
