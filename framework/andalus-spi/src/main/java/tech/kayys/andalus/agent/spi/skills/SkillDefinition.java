package tech.kayys.andalus.agent.spi.skills;

import java.util.List;
import java.util.Map;

public record SkillDefinition(
        String id,
        String name,
        String description,
        String category,
        String systemPrompt,
        Map<String, String> subSkillPrompts,
        String userPromptTemplate,
        Double temperature,
        Integer maxTokens,
        String defaultProvider,
        String fallbackProvider,
        List<String> tools,
        OrchestrationConfig orchestration,
        Map<String, Object> metadata) {

    public SkillDefinition {
        subSkillPrompts = subSkillPrompts != null ? Map.copyOf(subSkillPrompts) : Map.of();
        tools = tools != null ? List.copyOf(tools) : List.of();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public boolean hasSubSkills() {
        return !subSkillPrompts.isEmpty();
    }

    public boolean isOrchestrator() {
        return orchestration != null;
    }

    public String effectiveSystemPrompt(String fallback) {
        return (systemPrompt != null && !systemPrompt.isBlank()) ? systemPrompt : fallback;
    }

    public static Builder builder() {
        return new Builder();
    }

    public record OrchestrationConfig(
            String defaultType,
            String defaultStrategy,
            List<String> defaultChildSkills,
            Integer maxIterations,
            Integer maxDelegations) {

        public OrchestrationConfig {
            defaultChildSkills = defaultChildSkills != null ? List.copyOf(defaultChildSkills) : List.of();
        }
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String category;
        private String systemPrompt;
        private Map<String, String> subSkillPrompts;
        private String userPromptTemplate;
        private Double temperature;
        private Integer maxTokens;
        private String defaultProvider;
        private String fallbackProvider;
        private List<String> tools;
        private OrchestrationConfig orchestration;
        private Map<String, Object> metadata;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder systemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; return this; }
        public Builder subSkillPrompts(Map<String, String> subSkillPrompts) { this.subSkillPrompts = subSkillPrompts; return this; }
        public Builder userPromptTemplate(String userPromptTemplate) { this.userPromptTemplate = userPromptTemplate; return this; }
        public Builder temperature(Double temperature) { this.temperature = temperature; return this; }
        public Builder maxTokens(Integer maxTokens) { this.maxTokens = maxTokens; return this; }
        public Builder defaultProvider(String defaultProvider) { this.defaultProvider = defaultProvider; return this; }
        public Builder fallbackProvider(String fallbackProvider) { this.fallbackProvider = fallbackProvider; return this; }
        public Builder tools(List<String> tools) { this.tools = tools; return this; }
        public Builder orchestration(OrchestrationConfig orchestration) { this.orchestration = orchestration; return this; }
        public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }

        public SkillDefinition build() {
            return new SkillDefinition(id, name, description, category, systemPrompt,
                    subSkillPrompts, userPromptTemplate, temperature, maxTokens,
                    defaultProvider, fallbackProvider, tools, orchestration, metadata);
        }
    }
}
