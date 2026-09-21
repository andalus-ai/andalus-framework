package tech.kayys.andalus.skill.spi;

import java.util.Map;
import java.util.Set;
import tech.kayys.andalus.definition.Definition;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.extension.Reference;
import tech.kayys.andalus.identity.ResourceId;

public abstract class SkillDefinition extends Definition {
    public SkillDefinition(ResourceId id, Metadata metadata, Set<Reference> dependencies, Map<String, Object> configuration) {
        super(id, metadata, dependencies, configuration);
    }
    
    public abstract SkillDescriptor descriptor();
}
