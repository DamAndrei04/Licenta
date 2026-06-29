package com.uibuilder.mas.agent.agent.analyst.extractor;

import com.uibuilder.mas.agent.agent.analyst.model.Conflict;
import com.uibuilder.mas.agent.agent.analyst.model.ConflictSeverity;
import com.uibuilder.mas.agent.agent.analyst.model.Constraint;
import com.uibuilder.mas.agent.agent.analyst.model.Goal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Detectează conflictele dintre obiective și constrângeri. În prezent conține doar o
 * logică minimală (semnalează constrângerile nesatisfăcute), fiind un punct de extindere.
 */
@Slf4j
@Component
public class ConflictDetector {

    /**
     * Detectează conflictele dintre obiectivele și constrângerile date. În implementarea
     * curentă generează câte un conflict pentru fiecare constrângere nesatisfăcută.
     *
     * @param goals lista obiectivelor analizate
     * @param constraints lista constrângerilor analizate
     * @return lista conflictelor detectate (posibil goală)
     */
    public List<Conflict> detectConflicts(List<Goal> goals, List<Constraint> constraints) {
        log.debug("Detecting conflicts between {} goals and {} constraints", 
                goals.size(), constraints.size());
        
        List<Conflict> conflicts = new ArrayList<>();
        
        // Placeholder: Simple unsatisfied constraint detection
        constraints.stream()
                .filter(c -> !c.isSatisfied())
                .forEach(constraint -> {
                    Conflict conflict = Conflict.builder()
                            .id(UUID.randomUUID().toString())
                            .description("Constraint not satisfied: " + constraint.getDescription())
                            .severity(ConflictSeverity.MEDIUM)
                            .involvedGoalIds(List.of())
                            .involvedConstraintIds(List.of(constraint.getId()))
                            .resolved(false)
                            .build();
                    
                    conflicts.add(conflict);
                });
        
        return conflicts;
    }
}
