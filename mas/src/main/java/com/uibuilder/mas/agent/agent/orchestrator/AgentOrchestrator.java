package com.uibuilder.mas.agent.agent.orchestrator;

import com.uibuilder.mas.agent.agent.analyst.AnalystAgent;
import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.builder.BuilderAgent;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.PlannerAgent;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.agent.validator.ValidatorAgent;
import com.uibuilder.mas.agent.agent.validator.ValidationResult;
import com.uibuilder.mas.agent.memory.Blackboard;
import com.uibuilder.mas.api.dto.AgentPhase;
import com.uibuilder.mas.api.dto.AgentStatusEvent;
import com.uibuilder.mas.app.AgentStatusPublisher;
import com.uibuilder.mas.app.GenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Orchestrates agent execution flow.
 * Agents do not call each other — orchestrator coordinates via blackboard.
 *
 * If the validator rejects the output the entire pipeline is restarted from
 * scratch so each agent gets a fresh chance to produce valid output. After
 * MAX_ATTEMPTS failures a GenerationException is thrown to avoid burning
 * further tokens on a request that cannot succeed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentOrchestrator {

    /** Total number of full pipeline attempts before giving up. */
    public static final int MAX_ATTEMPTS = 3;

    private final AnalystAgent analystAgent;
    private final PlannerAgent plannerAgent;
    private final BuilderAgent builderAgent;
    private final ValidatorAgent validatorAgent;
    private final Blackboard blackboard;
    private final AgentStatusPublisher statusPublisher;

    public AgentExecutionContext execute(String userRequirement) {
        log.info("=== Starting MAS Execution Pipeline (max {} attempts) ===", MAX_ATTEMPTS);

        ValidationResult lastValidation = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            if (attempt > 1) {
                log.warn("Attempt {}/{}: restarting pipeline after validation failure: {}",
                        attempt, MAX_ATTEMPTS, lastValidation.getViolations());
                statusPublisher.emit(AgentStatusEvent.builder()
                        .status("RETRY")
                        .message("Validation failed — retrying (" + attempt + "/" + MAX_ATTEMPTS + ")")
                        .attemptNumber(attempt)
                        .maxAttempts(MAX_ATTEMPTS)
                        .violations(lastValidation.getViolations())
                        .build());
            }

            AgentExecutionContext context = new AgentExecutionContext();

            // ── Phase 1: Analysis ────────────────────────────────────────────
            log.info("[attempt {}/{}] Phase 1: Analysis", attempt, MAX_ATTEMPTS);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.ANALYST)
                    .status("STARTED")
                    .message("Analyzing your request")
                    .build());
            AnalyzedUIModel analyzedModel = analystAgent.analyze(userRequirement);
            blackboard.getAnalysisMemory().storeAnalysis(analyzedModel);
            context.setAnalyzedModel(analyzedModel);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.ANALYST)
                    .status("COMPLETED")
                    .build());

            // ── Phase 2: Planning ────────────────────────────────────────────
            log.info("[attempt {}/{}] Phase 2: Planning", attempt, MAX_ATTEMPTS);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.PLANNER)
                    .status("STARTED")
                    .message("Planning the UI layout")
                    .build());
            UIPlan plan = plannerAgent.createPlan(analyzedModel);
            blackboard.getPlanningMemory().storePlan(plan);
            context.setPlan(plan);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.PLANNER)
                    .status("COMPLETED")
                    .build());

            // ── Phase 3: Building ────────────────────────────────────────────
            log.info("[attempt {}/{}] Phase 3: Building", attempt, MAX_ATTEMPTS);
            int totalPages = plan.getPages() != null ? plan.getPages().size() : 0;
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.BUILDER)
                    .status("STARTED")
                    .message("Building components")
                    .totalPages(totalPages)
                    .build());
            UIComponentTree componentTree = builderAgent.build(plan);
            context.setComponentTree(componentTree);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.BUILDER)
                    .status("COMPLETED")
                    .build());

            // ── Phase 4: Validation ──────────────────────────────────────────
            log.info("[attempt {}/{}] Phase 4: Validation", attempt, MAX_ATTEMPTS);
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.VALIDATOR)
                    .status("STARTED")
                    .message("Validating the generated UI")
                    .build());
            ValidationResult validationResult = validatorAgent.validate(componentTree);
            blackboard.getValidationMemory().storeValidation(validationResult);
            context.setValidationResult(validationResult);
            lastValidation = validationResult;
            statusPublisher.emit(AgentStatusEvent.builder()
                    .phase(AgentPhase.VALIDATOR)
                    .status("COMPLETED")
                    .build());

            if (validationResult.isValid()) {
                log.info("=== MAS Execution Complete (succeeded on attempt {}/{}) ===",
                        attempt, MAX_ATTEMPTS);
                return context;
            }

            log.warn("[attempt {}/{}] Validation rejected output: {}",
                    attempt, MAX_ATTEMPTS, validationResult.getViolations());
        }

        // Every attempt produced an invalid result — give up.
        statusPublisher.emit(AgentStatusEvent.builder()
                .status("FAILED")
                .message("Generation failed after " + MAX_ATTEMPTS + " attempts")
                .attemptNumber(MAX_ATTEMPTS)
                .maxAttempts(MAX_ATTEMPTS)
                .violations(lastValidation != null ? lastValidation.getViolations() : java.util.List.of())
                .build());

        throw new GenerationException(
                "Generation failed after " + MAX_ATTEMPTS + " attempts. " +
                "Last violations: " + (lastValidation != null ? lastValidation.getViolations() : "none"),
                lastValidation != null ? lastValidation.getViolations() : java.util.List.of(),
                MAX_ATTEMPTS
        );
    }
}
