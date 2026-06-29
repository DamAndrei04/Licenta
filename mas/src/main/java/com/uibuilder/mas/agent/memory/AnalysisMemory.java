package com.uibuilder.mas.agent.memory;

import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Segment de memorie pentru stocarea rezultatelor analizei (modelele UI analizate).
 */
@Slf4j
public class AnalysisMemory {

    private final List<AnalyzedUIModel> analyses = new ArrayList<>();

    /**
     * Stochează un model UI analizat.
     *
     * @param model modelul analizat care trebuie stocat
     */
    public void storeAnalysis(AnalyzedUIModel model) {
        log.debug("Storing analysis: {}", model.getAnalysisId());
        analyses.add(model);
    }

    /**
     * Returnează cel mai recent model analizat.
     *
     * @return un {@link Optional} cu ultimul model analizat sau gol dacă nu există niciunul
     */
    public Optional<AnalyzedUIModel> getLatestAnalysis() {
        if (analyses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(analyses.get(analyses.size() - 1));
    }

    /**
     * Returnează toate modelele analizate stocate.
     *
     * @return o copie a listei de modele analizate
     */
    public List<AnalyzedUIModel> getAllAnalyses() {
        return new ArrayList<>(analyses);
    }

    /**
     * Golește segmentul de memorie.
     */
    public void clear() {
        analyses.clear();
    }
}
