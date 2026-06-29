package com.uibuilder.mas.agent.agent.analyst;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;

/**
 * Interfața agentului Analist — analizează cerințele utilizatorului și extrage modelul
 * semantic al interfeței. Raționamentul este delegat extractoarelor bazate pe LLM.
 */
public interface AnalystAgent {

    /**
     * Analizează o cerință a utilizatorului și produce un model semantic.
     *
     * @param userRequirement cerința utilizatorului (ex. „Creează un site de tip CV...”)
     * @return modelul analizat (imutabil)
     */
    AnalyzedUIModel analyze(String userRequirement);

    /**
     * Returnează identificatorul agentului.
     *
     * @return identificatorul agentului
     */
    String getAgentId();
}
