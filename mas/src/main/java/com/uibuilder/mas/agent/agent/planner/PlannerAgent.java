package com.uibuilder.mas.agent.agent.planner;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;

/**
 * Interfața agentului Planificator — creează planuri de execuție pornind de la modelele
 * analizate. Nu implementează direct logica de planificare, ci o deleagă strategiilor.
 */
public interface PlannerAgent {

    /**
     * Generează un plan de construire a interfeței pe baza modelului analizat.
     *
     * @param analyzedModel modelul semantic analizat
     * @return planul de execuție (imutabil)
     */
    UIPlan createPlan(AnalyzedUIModel analyzedModel);

    /**
     * Returnează identificatorul agentului.
     *
     * @return identificatorul agentului
     */
    String getAgentId();
}
