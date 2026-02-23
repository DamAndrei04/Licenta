package com.uibuilder.mas.agent.analyst.extractor;

import com.uibuilder.mas.agent.analyst.model.Conflict;
import com.uibuilder.mas.agent.analyst.model.ConflictSeverity;
import com.uibuilder.mas.agent.analyst.model.Constraint;
import com.uibuilder.mas.agent.analyst.model.Goal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Detects conflicts between goals and constraints.
 * Contains placeholder logic only.
 */
@Slf4j
@Component
public class ConflictDetector {
    
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
