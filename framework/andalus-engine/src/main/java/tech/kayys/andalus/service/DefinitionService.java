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
import java.util.Optional;

import tech.kayys.andalus.core.AgentDefinition;
import tech.kayys.andalus.skill.spi.SkillDefinition;
import tech.kayys.andalus.workflow.WorkflowDefinition;

/**
 * Definition Service - manages definitions
 */
public interface DefinitionService {
    void registerAgent(AgentDefinition agent);
    void registerSkill(SkillDefinition skill);
    void registerWorkflow(WorkflowDefinition workflow);
    
    Optional<AgentDefinition> findAgent(String id);
    Optional<AgentDefinition> findAgentByName(String name);
    Optional<SkillDefinition> findSkill(String id);
    Optional<SkillDefinition> findSkillByName(String name);
    Optional<WorkflowDefinition> findWorkflow(String id);
    Optional<WorkflowDefinition> findWorkflowByName(String name);
    
    List<AgentDefinition> listAgents();
    List<SkillDefinition> listSkills();
    List<WorkflowDefinition> listWorkflows();
    
    void removeAgent(String id);
    void removeSkill(String id);
    void removeWorkflow(String id);
}
