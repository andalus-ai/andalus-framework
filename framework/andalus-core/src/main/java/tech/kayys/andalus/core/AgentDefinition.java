package tech.kayys.andalus.core;

import java.util.*;

import tech.kayys.andalus.definition.Definition;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.extension.Reference;
import tech.kayys.andalus.descriptor.Descriptor;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.spi.agent.AgentContract;
import tech.kayys.andalus.spi.agent.AgentIdentity;

/**
 * Agent definition - describes what an agent is.
 * An AgentDefinition describes the logical composition of an agent.
 * It does not contain execution state and must not own runtime resources.
 */
public final class AgentDefinition extends Definition {
    
    private final AgentIdentity identity;
    private final String role;
    private final String goal;
    private final String instructions;
    private final Set<String> capabilities;
    private final List<Reference> skills;
    private final List<Reference> tools;
    private final Reference workflow;
    private final Reference memory;
    private final Reference knowledge;
    private final Reference planner;
    private final Reference reasoner;
    private final Reference model;
    private final Map<String, Object> constraints;
    private final AgentContract contract;
    
    private AgentDefinition(Builder builder) {
        super(
            new ResourceId.AgentId(Id.random()),
            builder.metadata,
            builder.dependencies,
            builder.configuration
        );
        this.identity = builder.identity;
        this.role = builder.role != null ? builder.role : "";
        this.goal = builder.goal != null ? builder.goal : "";
        this.instructions = builder.instructions != null ? builder.instructions : "";
        this.capabilities = Set.copyOf(builder.capabilities);
        this.skills = builder.skills != null ? List.copyOf(builder.skills) : List.of();
        this.tools = builder.tools != null ? List.copyOf(builder.tools) : List.of();
        this.workflow = builder.workflow;
        this.memory = builder.memory;
        this.knowledge = builder.knowledge;
        this.planner = builder.planner;
        this.reasoner = builder.reasoner;
        this.model = builder.model;
        this.constraints = builder.constraints != null ? Map.copyOf(builder.constraints) : Map.of();
        this.contract = builder.contract != null 
                ? builder.contract 
                : AgentDefinitionContractFactory.create(this);
    }
    
    public AgentIdentity identity() { return identity; }
    public String role() { return role; }
    public String goal() { return goal; }
    public String instructions() { return instructions; }
    public Set<String> capabilities() { return capabilities; }
    public List<Reference> skills() { return skills; }
    public List<Reference> tools() { return tools; }
    public Reference workflow() { return workflow; }
    public Reference memory() { return memory; }
    public Reference knowledge() { return knowledge; }
    public Reference planner() { return planner; }
    public Reference reasoner() { return reasoner; }
    public Reference model() { return model; }
    public Map<String, Object> constraints() { return constraints; }
    public AgentContract contract() { return contract; }
    
    public Optional<Reference> workflowReference() {
        return Optional.ofNullable(workflow);
    }

    public Optional<Reference> memoryReference() {
        return Optional.ofNullable(memory);
    }

    public Optional<Reference> knowledgeReference() {
        return Optional.ofNullable(knowledge);
    }

    public Optional<Reference> plannerReference() {
        return Optional.ofNullable(planner);
    }

    public Optional<Reference> reasonerReference() {
        return Optional.ofNullable(reasoner);
    }

    public Optional<Reference> modelReference() {
        return Optional.ofNullable(model);
    }

    public boolean requiresCapability(String capability) {
        return capabilities.contains(capability);
    }

    public Set<String> requiredCapabilityIds() {
        return capabilities;
    }

    public boolean hasSkill(String skillId) {
        return skills.stream()
                .anyMatch(ref -> skillId.equals(ref.name()) || (ref.id() != null && skillId.equals(ref.id().asString())));
    }

    public boolean hasTool(String toolId) {
        return tools.stream()
                .anyMatch(ref -> toolId.equals(ref.name()) || (ref.id() != null && toolId.equals(ref.id().asString())));
    }
    
    @Override
    public Descriptor descriptor() {
        return new AgentDescriptor(
            id().value().asString(),
            metadata().name() != null ? metadata().name() : identity.id(),
            metadata().version() != null ? metadata().version().toString() : "1.0.0",
            metadata().description() != null ? metadata().description() : goal,
            Set.of(role != null && !role.isBlank() ? role : "agent"),
            Set.of("agent"),
            Map.of(),
            Map.of(),
            List.of(),
            List.of()
        );
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Metadata metadata = Metadata.empty();
        private AgentIdentity identity;
        private String role;
        private String goal;
        private String instructions;
        private Set<String> capabilities = new HashSet<>();
        private List<Reference> skills = new ArrayList<>();
        private List<Reference> tools = new ArrayList<>();
        private Reference workflow;
        private Reference memory;
        private Reference knowledge;
        private Reference planner;
        private Reference reasoner;
        private Reference model;
        private Map<String, Object> constraints = new HashMap<>();
        private final Set<Reference> dependencies = new HashSet<>();
        private final Map<String, Object> configuration = new HashMap<>();
        private AgentContract contract;
        
        public Builder identity(AgentIdentity identity) {
            this.identity = identity;
            return this;
        }

        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public Builder goal(String goal) {
            this.goal = goal;
            return this;
        }
        
        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }
        
        public Builder capability(String capability) {
            if (capability != null) {
                this.capabilities.add(capability);
            }
            return this;
        }
        
        public Builder capabilities(Set<String> capabilities) {
            if (capabilities != null) {
                this.capabilities.addAll(capabilities);
            }
            return this;
        }
        
        public Builder skill(Reference skill) {
            if (skill != null) {
                this.skills.add(skill);
                this.dependencies.add(skill);
            }
            return this;
        }
        
        public Builder skills(Reference... skills) {
            if (skills != null) {
                for (Reference s : skills) {
                    skill(s);
                }
            }
            return this;
        }
        
        public Builder skills(List<Reference> skills) {
            if (skills != null) {
                for (Reference s : skills) {
                    skill(s);
                }
            }
            return this;
        }
        
        public Builder tool(Reference tool) {
            if (tool != null) {
                this.tools.add(tool);
                this.dependencies.add(tool);
            }
            return this;
        }
        
        public Builder tools(Reference... tools) {
            if (tools != null) {
                for (Reference t : tools) {
                    tool(t);
                }
            }
            return this;
        }
        
        public Builder tools(List<Reference> tools) {
            if (tools != null) {
                for (Reference t : tools) {
                    tool(t);
                }
            }
            return this;
        }
        
        public Builder workflow(Reference workflow) {
            this.workflow = workflow;
            if (workflow != null) this.dependencies.add(workflow);
            return this;
        }
        
        public Builder memory(Reference memory) {
            this.memory = memory;
            if (memory != null) this.dependencies.add(memory);
            return this;
        }
        
        public Builder knowledge(Reference knowledge) {
            this.knowledge = knowledge;
            if (knowledge != null) this.dependencies.add(knowledge);
            return this;
        }
        
        public Builder planner(Reference planner) {
            this.planner = planner;
            if (planner != null) this.dependencies.add(planner);
            return this;
        }
        
        public Builder reasoner(Reference reasoner) {
            this.reasoner = reasoner;
            if (reasoner != null) this.dependencies.add(reasoner);
            return this;
        }
        
        public Builder model(Reference model) {
            this.model = model;
            if (model != null) this.dependencies.add(model);
            return this;
        }
        
        public Builder constraint(String key, Object value) {
            this.constraints.put(key, value);
            return this;
        }
        
        public Builder constraints(Map<String, Object> constraints) {
            if (constraints != null) {
                this.constraints.putAll(constraints);
            }
            return this;
        }
        
        public Builder configuration(Map<String, Object> configuration) {
            if (configuration != null) {
                this.configuration.putAll(configuration);
            }
            return this;
        }
        
        public Builder contract(AgentContract contract) {
            this.contract = contract;
            return this;
        }
        
        public AgentDefinition build() {
            if (identity == null) {
                if (metadata != null && metadata.name() != null && !metadata.name().isBlank()) {
                    identity = AgentIdentity.of(metadata.name().toLowerCase().replaceAll("[^a-z0-9_-]", "-"));
                } else {
                    identity = AgentIdentity.of("agent-" + UUID.randomUUID());
                }
            }
            if (metadata == null || metadata.name() == null || metadata.name().isBlank()) {
                metadata = Metadata.builder()
                        .name(identity.id())
                        .description(role != null ? role : "")
                        .build();
            }
            return new AgentDefinition(this);
        }
    }
}
