package tech.kayys.andalus.reasoner;
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
import java.util.concurrent.CompletableFuture;

import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.context.ContextData;
import tech.kayys.andalus.planner.Plan;
import tech.kayys.andalus.execution.ExecutionContext;


/**
 * Reasoner - performs reasoning on plans.
 */
public interface Reasoner extends Extension {
    
    /**
     * Reason on a plan
     */
    ReasoningResult reason(Plan plan, ExecutionContext context) throws Exception;
    
    /**
     * Reason on a plan with context data
     */
    default ReasoningResult reason(Plan plan, ExecutionContext context, ContextData contextData) throws Exception {
        return reason(plan, context);
    }
    
    /**
     * Reason asynchronously
     */
    default CompletableFuture<ReasoningResult> reasonAsync(Plan plan, ExecutionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return reason(plan, context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Get supported reasoning strategies
     */
    default Set<ReasoningStrategy> getSupportedStrategies() {
        return Set.of(ReasoningStrategy.CHAIN_OF_THOUGHT);
    }
}