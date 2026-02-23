package com.uibuilder.mas.memory;

import com.uibuilder.mas.agent.planner.model.UIPlan;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Memory segment for storing execution plans.
 */
@Slf4j
public class PlanningMemory {
    
    private final List<UIPlan> plans = new ArrayList<>();
    
    public void storePlan(UIPlan plan) {
        log.debug("Storing plan: {}", plan.getPlanId());
        plans.add(plan);
    }
    
    public Optional<UIPlan> getLatestPlan() {
        if (plans.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(plans.get(plans.size() - 1));
    }
    
    public List<UIPlan> getAllPlans() {
        return new ArrayList<>(plans);
    }
    
    public void clear() {
        plans.clear();
    }
}
