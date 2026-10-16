package tech.kayys.andalus.skill.tool;

import tech.kayys.andalus.skill.spi.SkillDescriptor;

/**
 * Factory that converts a {@link SkillDescriptor} into a {@link SkillToolDescriptor}.
 *
 * <p>Allows customisation of the mapping (e.g., name aliasing, schema
 * generation) without changing the core Skill SPI.</p>
 */
public interface SkillToolDescriptorFactory {

    /**
     * Create a Tool descriptor from the given Skill descriptor.
     *
     * @param skillDescriptor the canonical Skill descriptor; never {@code null}
     * @return a Tool descriptor suitable for registering the Skill as a Tool
     */
    SkillToolDescriptor create(SkillDescriptor skillDescriptor);
}
