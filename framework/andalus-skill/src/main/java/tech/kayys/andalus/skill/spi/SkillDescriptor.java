package tech.kayys.andalus.skill.spi;

import tech.kayys.andalus.descriptor.Descriptor;

public interface SkillDescriptor extends Descriptor {
    // Inherits id() as ResourceId
    // Inherits metadata() as Metadata
    // Inherits tags() as Set<String>
    
    String category();
}
