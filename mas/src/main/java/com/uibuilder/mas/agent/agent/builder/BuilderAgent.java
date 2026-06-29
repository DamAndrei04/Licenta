package com.uibuilder.mas.agent.agent.builder;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;

/**
 * Interfața agentului Constructor (Builder) — construiește componentele UI pornind de la
 * planuri. Nu generează direct interfața, ci deleagă generarea către generatoare.
 */
public interface BuilderAgent {

    /**
     * Construiește arborele de componente UI pe baza planului de execuție.
     *
     * @param plan planul de execuție
     * @return arborele de componente (imutabil)
     */
    UIComponentTree build(UIPlan plan);

    /**
     * Returnează identificatorul agentului.
     *
     * @return identificatorul agentului
     */
    String getAgentId();
}
