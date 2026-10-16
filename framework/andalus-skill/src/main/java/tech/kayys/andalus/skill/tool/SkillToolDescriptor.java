package tech.kayys.andalus.skill.tool;

import tech.kayys.andalus.skill.spi.SkillDescriptor;

import java.util.Objects;

/**
 * A descriptor projection of a Skill into the Tool calling system.
 *
 * <p>Produced by {@link SkillToolDescriptorFactory}. Consumers in the
 * ToolRuntime layer can use this to register a Skill as a callable Tool
 * without knowing Andalus internals.</p>
 */
public record SkillToolDescriptor(
        String id,
        String name,
        String description,
        String version,
        String category,
        SkillDescriptor source
) {

    public SkillToolDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        description = description == null ? "" : description;
        version     = version     == null ? "" : version;
        category    = category    == null ? "" : category;
    }

    /**
     * Derive a {@code SkillToolDescriptor} from the canonical Skill descriptor.
     * Maps id, name, description, version, and category directly.
     */
    public static SkillToolDescriptor from(SkillDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        return new SkillToolDescriptor(
                descriptor.id().asString(),
                descriptor.name(),
                descriptor.description(),
                descriptor.version(),
                descriptor.category(),
                descriptor);
    }
}
