package com.uibuilder.mas.app;

import java.util.List;

/**
 * Thrown by the orchestrator when the validator rejects the generated output
 * on every allowed attempt.
 */
public class GenerationException extends RuntimeException {

    private final List<String> violations;
    private final int attempts;

    public GenerationException(String message, List<String> violations, int attempts) {
        super(message);
        this.violations = violations;
        this.attempts = attempts;
    }

    public List<String> getViolations() {
        return violations;
    }

    public int getAttempts() {
        return attempts;
    }
}
