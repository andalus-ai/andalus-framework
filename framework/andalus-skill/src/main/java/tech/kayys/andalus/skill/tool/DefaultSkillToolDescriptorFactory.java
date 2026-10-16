package tech.kayys.andalus.skill.tool;

import tech.kayys.andalus.skill.spi.SkillDescriptor;

/**
 * Default implementation of {@link SkillToolDescriptorFactory}.
 * Maps Skill metadata fields directly to Tool descriptor fields with no
 * transformation, using {@link SkillToolDescriptor#from(SkillDescriptor)}.
 */
public final class DefaultSkillToolDescriptorFactory
        implements SkillToolDescriptorFactory {

    @Override
    public SkillToolDescriptor create(SkillDescriptor skillDescriptor) {
        return SkillToolDescriptor.from(skillDescriptor);
    }
}
