package com.uibuilder.mas.agent.memory;

import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Segment de memorie pentru stocarea planurilor de execuție.
 */
@Slf4j
public class PlanningMemory {

    private final List<UIPlan> plans = new ArrayList<>();

    /**
     * Stochează un plan de execuție.
     *
     * @param plan planul care trebuie stocat
     */
    public void storePlan(UIPlan plan) {
        log.debug("Storing plan: {}", plan.getPlanId());
        plans.add(plan);
    }

    /**
     * Returnează cel mai recent plan stocat.
     *
     * @return un {@link Optional} cu ultimul plan sau gol dacă nu există niciunul
     */
    public Optional<UIPlan> getLatestPlan() {
        if (plans.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(plans.get(plans.size() - 1));
    }

    /**
     * Returnează toate planurile stocate.
     *
     * @return o copie a listei de planuri
     */
    public List<UIPlan> getAllPlans() {
        return new ArrayList<>(plans);
    }

    /**
     * Golește segmentul de memorie.
     */
    public void clear() {
        plans.clear();
    }
}
