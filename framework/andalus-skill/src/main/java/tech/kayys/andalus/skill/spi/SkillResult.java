package tech.kayys.andalus.skill.spi;

import java.util.Map;

public interface SkillResult {
    Map<String, Object> getOutputs();
    boolean isSuccess();
}
