package tech.kayys.andalus.skill.spi;

import java.util.Map;

public interface SkillContext {
    Map<String, Object> getInputs();
}
