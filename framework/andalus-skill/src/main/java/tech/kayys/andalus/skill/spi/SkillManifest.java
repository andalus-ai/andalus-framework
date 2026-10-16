package tech.kayys.andalus.skill.spi;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record SkillManifest(
        String id,
        String version,
        String name,
        String description,
        SkillKind kind,
        String category,
        Set<String> capabilities,
        List<SkillReference> skills,
        List<SkillReference> tools,
        Set<String> dependencies,
        Map<String, Object> configuration,
        Map<String, Object> metadata
) {

    public SkillManifest {
        requireText(id, "id");
        requireText(version, "version");
        requireText(name, "name");

        description = description == null
                ? ""
                : description.trim();

        Objects.requireNonNull(kind, "kind must not be null");

        category = category == null
                ? ""
                : category.trim();

        capabilities = capabilities == null
                ? Set.of()
                : Set.copyOf(capabilities);

        skills = skills == null
                ? List.of()
                : List.copyOf(skills);

        tools = tools == null
                ? List.of()
                : List.copyOf(tools);

        dependencies = dependencies == null
                ? Set.of()
                : Set.copyOf(dependencies);

        configuration = configuration == null
                ? Map.of()
                : Map.copyOf(configuration);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    private static void requireText(
            String value,
            String field) {

        Objects.requireNonNull(
                value,
                field + " must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " must not be blank");
        }
    }

    public boolean requiresCapability(String capability) {
        return capabilities.contains(capability);
    }

    public boolean usesTool(String toolId) {
        return tools.stream()
                .anyMatch(reference ->
                        reference.id().equals(toolId));
    }

    public boolean usesSkill(String skillId) {
        return skills.stream()
                .anyMatch(reference ->
                        reference.id().equals(skillId));
    }
}
