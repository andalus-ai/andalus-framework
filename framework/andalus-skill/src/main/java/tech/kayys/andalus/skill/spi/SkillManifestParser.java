package tech.kayys.andalus.skill.spi;

import java.util.Map;

public interface SkillManifestParser {

    SkillManifest parse(Map<String, Object> manifest);

    Map<String, Object> serialize(SkillManifest manifest);
}
