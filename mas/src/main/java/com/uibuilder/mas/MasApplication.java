package com.uibuilder.mas;

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
import com.uibuilder.mas.util.JsonUtils;
import com.uibuilder.mas.util.SchemaTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Multi-Agent System (MAS) Application for UI Builder with Anthropic LLM integration.
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class MasApplication {
    
    private final AnalystAgent analystAgent;
    private final PlannerAgent plannerAgent;
    private final BuilderAgent builderAgent;
    private final ValidatorAgent validatorAgent;
    private final Blackboard blackboard;
    private final JsonUtils jsonUtils;
    private final SchemaTransformer schemaTransformer;

    public static void main(String[] args) {
        SpringApplication.run(MasApplication.class, args);
    }

    @Bean
    public CommandLineRunner runMAS() {
        return args -> {
            // Example user requirement
            String userRequirement = "Build an interface for a food delivery web platform";


        
            log.info("\n" + "=".repeat(80));
            log.info("🚀 STARTING MULTI-AGENT SYSTEM");
            log.info("=".repeat(80));
            log.info("User Requirement: {}", userRequirement);
            log.info("=".repeat(80) + "\n");
        
            try {
                // Phase 1: Analyst
                log.info("📊 Phase 1: Analysis");
                AnalyzedUIModel analyzedModel = analystAgent.analyze(userRequirement);
            
                // Phase 2: Planner
                log.info("\n📋 Phase 2: Planning");
                UIPlan plan = plannerAgent.createPlan(analyzedModel);
            
                // Phase 3: Builder
                log.info("\n🏗️ Phase 3: Building");
                UIComponentTree componentTree = builderAgent.build(plan);
            
                // Phase 4: Validator
                log.info("\n✅ Phase 4: Validation");
                ValidationResult validationResult = validatorAgent.validate(componentTree);
            
                // Phase 5: Transform to schema-compliant format
                log.info("\n🔄 Phase 5: Schema Transformation");
                UIDescriptor finalDescriptor = schemaTransformer.transform(componentTree);
            
                // Final Output
                log.info("\n" + "=".repeat(80));
                log.info("🎉 FINAL UI DESCRIPTOR JSON (ui-descriptor-v1.json compliant):");
                log.info("=".repeat(80));
            
                String finalJson = jsonUtils.toJson(finalDescriptor);
                System.out.println(finalJson);
            
                log.info("\n" + "=".repeat(80));
                log.info("✅ MAS EXECUTION COMPLETE");
                log.info("=".repeat(80));
                log.info("Status: {}", validationResult.isValid() ? "SUCCESS" : "COMPLETED WITH WARNINGS");
                log.info("Total Messages: {}", blackboard.getAllMessages().size());
                log.info("Total Components: {}", finalDescriptor.getPages().get("page-home").getDroppedItems().size());
                log.info("=".repeat(80) + "\n");
            
            } catch (Exception e) {
                log.error("Error during MAS execution", e);
                System.exit(1);
            }
        };
    }
}


