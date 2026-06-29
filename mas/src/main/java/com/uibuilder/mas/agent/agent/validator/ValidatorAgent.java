package com.uibuilder.mas.agent.agent.validator;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;

/**
 * Interfața agentului Validator — validează componentele UI construite. Logica de
 * validare este delegată regulilor și verificărilor structurale.
 */
public interface ValidatorAgent {

    /**
     * Validează un arbore de componente UI construit.
     *
     * @param componentTree arborele de componente care trebuie validat
     * @return rezultatul validării
     */
    ValidationResult validate(UIComponentTree componentTree);

    /**
     * Returnează identificatorul agentului.
     *
     * @return identificatorul agentului
     */
    String getAgentId();
}
