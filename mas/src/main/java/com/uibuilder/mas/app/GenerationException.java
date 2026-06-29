package com.uibuilder.mas.app;

import java.util.List;

/**
 * Excepție aruncată de orchestrator atunci când validatorul respinge rezultatul generat la
 * fiecare dintre încercările permise. Reține încălcările raportate și numărul de încercări.
 */
public class GenerationException extends RuntimeException {

    private final List<String> violations;
    private final int attempts;

    /**
     * Construiește excepția cu un mesaj, lista încălcărilor și numărul de încercări efectuate.
     *
     * @param message mesajul de eroare
     * @param violations lista încălcărilor raportate de validator la ultima încercare
     * @param attempts numărul de încercări efectuate înainte de eșec
     */
    public GenerationException(String message, List<String> violations, int attempts) {
        super(message);
        this.violations = violations;
        this.attempts = attempts;
    }

    /**
     * Returnează lista încălcărilor raportate de validator.
     *
     * @return lista încălcărilor
     */
    public List<String> getViolations() {
        return violations;
    }

    /**
     * Returnează numărul de încercări efectuate înainte de eșec.
     *
     * @return numărul de încercări
     */
    public int getAttempts() {
        return attempts;
    }
}
