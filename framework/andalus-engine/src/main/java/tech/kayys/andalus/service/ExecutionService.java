package tech.kayys.andalus.service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import tech.kayys.andalus.core.AgentDefinition;
import tech.kayys.andalus.skill.spi.SkillDefinition;
import tech.kayys.andalus.workflow.WorkflowDefinition;
import tech.kayys.andalus.identity.ResourceId.CorrelationId;
import tech.kayys.andalus.execution.GraphExecutionContext;
import tech.kayys.andalus.execution.ExecutionState;

/**
 * Execution Service - executes definitions
 */
public interface ExecutionService {
    GraphExecutionContext startAgent(String agentId, Map<String, Object> inputs);
    GraphExecutionContext startSkill(String skillId, Map<String, Object> inputs);
    GraphExecutionContext startWorkflow(String workflowId, Map<String, Object> inputs);
    
    GraphExecutionContext startAgent(AgentDefinition agent, Map<String, Object> inputs);
    GraphExecutionContext startSkill(SkillDefinition skill, Map<String, Object> inputs);
    GraphExecutionContext startWorkflow(WorkflowDefinition workflow, Map<String, Object> inputs);
    
    CompletableFuture<GraphExecutionContext> startAgentAsync(String agentId, Map<String, Object> inputs);
    CompletableFuture<GraphExecutionContext> startSkillAsync(String skillId, Map<String, Object> inputs);
    CompletableFuture<GraphExecutionContext> startWorkflowAsync(String workflowId, Map<String, Object> inputs);
    
    Optional<GraphExecutionContext> getExecution(CorrelationId correlationId);
    Optional<GraphExecutionContext> getExecution(String correlationId);
    List<GraphExecutionContext> listExecutions();
    List<GraphExecutionContext> listExecutions(ExecutionState state);
    
    void pauseExecution(CorrelationId correlationId);
    void resumeExecution(CorrelationId correlationId);
    void cancelExecution(CorrelationId correlationId);
}
