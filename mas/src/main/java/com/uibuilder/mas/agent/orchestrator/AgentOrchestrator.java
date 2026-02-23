package com.uibuilder.mas.agent.orchestrator;

import com.uibuilder.mas.agent.analyst.AnalystAgent;
import com.uibuilder.mas.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.builder.BuilderAgent;
import com.uibuilder.mas.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.planner.PlannerAgent;
import com.uibuilder.mas.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.validator.ValidatorAgent;
import com.uibuilder.mas.agent.validator.ValidationResult;
import com.uibuilder.mas.descriptor.UIDescriptor;
import com.uibuilder.mas.memory.Blackboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Orchestrates agent execution flow.
 * Agents do not call each other - orchestrator coordinates via blackboard.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentOrchestrator {
    
    private final AnalystAgent analystAgent;
    private final PlannerAgent plannerAgent;
    private final BuilderAgent builderAgent;
    private final ValidatorAgent validatorAgent;
    private final Blackboard blackboard;
    
    /**
     * Execute full MAS pipeline: analyze -> plan -> build -> validate.
     */
    public AgentExecutionContext execute(String userRequirement) {
        log.info("=== Starting MAS Execution Pipeline ===");
        
        AgentExecutionContext context = new AgentExecutionContext();
        
        // Phase 1: Analysis
        log.info("Phase 1: Analysis");
        AnalyzedUIModel analyzedModel = analystAgent.analyze(userRequirement);
        blackboard.getAnalysisMemory().storeAnalysis(analyzedModel);
        context.setAnalyzedModel(analyzedModel);
        
        // Phase 2: Planning
        log.info("Phase 2: Planning");
        UIPlan plan = plannerAgent.createPlan(analyzedModel);
        blackboard.getPlanningMemory().storePlan(plan);
        context.setPlan(plan);
        
        // Phase 3: Building
        log.info("Phase 3: Building");
        UIComponentTree componentTree = builderAgent.build(plan);
        context.setComponentTree(componentTree);
        
        // Phase 4: Validation
        log.info("Phase 4: Validation");
        ValidationResult validationResult = validatorAgent.validate(componentTree);
        blackboard.getValidationMemory().storeValidation(validationResult);
        context.setValidationResult(validationResult);
        
        log.info("=== MAS Execution Complete ===");
        return context;
    }
}
