package com.uibuilder.mas.agent.agent.orchestrator;

import com.uibuilder.mas.agent.agent.analyst.AnalystAgent;
import com.uibuilder.mas.agent.agent.analyst.model.AnalyzedUIModel;
import com.uibuilder.mas.agent.agent.builder.BuilderAgent;
import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;
import com.uibuilder.mas.agent.agent.planner.PlannerAgent;
import com.uibuilder.mas.agent.agent.planner.model.UIPlan;
import com.uibuilder.mas.agent.agent.validator.ValidationResult;
import com.uibuilder.mas.agent.agent.validator.ValidatorAgent;
import com.uibuilder.mas.agent.memory.Blackboard;
import com.uibuilder.mas.app.AgentStatusPublisher;
import com.uibuilder.mas.app.GenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Basic unit tests for {@link AgentOrchestrator} using Mockito.
 * The four agents and the status publisher are mocked; a real (in-memory)
 * blackboard is used since it has no external dependencies. The tests cover
 * the happy path and the retry-then-fail path.
 */
@ExtendWith(MockitoExtension.class)
class AgentOrchestratorTest {

    @Mock
    private AnalystAgent analystAgent;
    @Mock
    private PlannerAgent plannerAgent;
    @Mock
    private BuilderAgent builderAgent;
    @Mock
    private ValidatorAgent validatorAgent;
    @Mock
    private AgentStatusPublisher statusPublisher;

    private AgentOrchestrator orchestrator;

    private final AnalyzedUIModel analyzedModel = mock(AnalyzedUIModel.class);
    private final UIPlan plan = mock(UIPlan.class);
    private final UIComponentTree componentTree = mock(UIComponentTree.class);

    @BeforeEach
    void setUp() {
        orchestrator = new AgentOrchestrator(
                analystAgent, plannerAgent, builderAgent, validatorAgent,
                new Blackboard(), statusPublisher);
    }

    @Test
    void execute_whenValidationPasses_returnsContextOnFirstAttempt() {
        when(analystAgent.analyze(anyString())).thenReturn(analyzedModel);
        when(plannerAgent.createPlan(any(AnalyzedUIModel.class))).thenReturn(plan);
        when(builderAgent.build(any(UIPlan.class))).thenReturn(componentTree);
        when(validatorAgent.validate(any(UIComponentTree.class)))
                .thenReturn(new ValidationResult(true, List.of()));

        AgentExecutionContext context = orchestrator.execute("Create a login page");

        assertTrue(context.getValidationResult().isValid());
        assertSame(analyzedModel, context.getAnalyzedModel());
        assertSame(plan, context.getPlan());
        assertSame(componentTree, context.getComponentTree());

        // Ran the pipeline exactly once (no retries).
        verify(analystAgent, times(1)).analyze("Create a login page");
        verify(validatorAgent, times(1)).validate(componentTree);
    }

    @Test
    void execute_whenValidationAlwaysFails_retriesMaxAttemptsThenThrows() {
        when(analystAgent.analyze(anyString())).thenReturn(analyzedModel);
        when(plannerAgent.createPlan(any(AnalyzedUIModel.class))).thenReturn(plan);
        when(builderAgent.build(any(UIPlan.class))).thenReturn(componentTree);
        when(validatorAgent.validate(any(UIComponentTree.class)))
                .thenReturn(new ValidationResult(false, List.of("missing page")));

        GenerationException exception = assertThrows(
                GenerationException.class,
                () -> orchestrator.execute("invalid request"));

        // The whole pipeline is restarted up to MAX_ATTEMPTS times.
        verify(analystAgent, times(AgentOrchestrator.MAX_ATTEMPTS)).analyze(anyString());
        verify(validatorAgent, times(AgentOrchestrator.MAX_ATTEMPTS)).validate(any(UIComponentTree.class));
        assertEquals(AgentOrchestrator.MAX_ATTEMPTS, exception.getAttempts());
    }
}
