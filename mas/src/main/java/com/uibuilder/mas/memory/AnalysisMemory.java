package com.uibuilder.mas.memory;

import com.uibuilder.mas.agent.analyst.model.AnalyzedUIModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Memory segment for storing analysis results.
 */
@Slf4j
public class AnalysisMemory {
    
    private final List<AnalyzedUIModel> analyses = new ArrayList<>();
    
    public void storeAnalysis(AnalyzedUIModel model) {
        log.debug("Storing analysis: {}", model.getAnalysisId());
        analyses.add(model);
    }
    
    public Optional<AnalyzedUIModel> getLatestAnalysis() {
        if (analyses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(analyses.get(analyses.size() - 1));
    }
    
    public List<AnalyzedUIModel> getAllAnalyses() {
        return new ArrayList<>(analyses);
    }
    
    public void clear() {
        analyses.clear();
    }
}
