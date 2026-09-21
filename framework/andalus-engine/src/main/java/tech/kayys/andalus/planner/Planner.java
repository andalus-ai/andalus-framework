package tech.kayys.andalus.planner;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.*;

import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.context.ContextData;
import tech.kayys.andalus.execution.ExecutionContext;


/**
 * Planner - creates execution plans.
 */
public interface Planner extends Extension {
    
    /**
     * Create a plan
     */
    Plan createPlan(ExecutionContext context) throws Exception;
    
    /**
     * Create a plan with context data
     */
    default Plan createPlan(ExecutionContext context, ContextData contextData) throws Exception {
        return createPlan(context);
    }
    
    /**
     * Create a plan asynchronously
     */
    default CompletableFuture<Plan> createPlanAsync(ExecutionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return createPlan(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Get supported planning strategies
     */
    default Set<PlanningStrategy> getSupportedStrategies() {
        return Set.of(PlanningStrategy.SEQUENTIAL);
    }
    
    /**
     * Validate a plan
     */
    default boolean validate(Plan plan) {
        return plan != null && !plan.steps().isEmpty();
    }
}