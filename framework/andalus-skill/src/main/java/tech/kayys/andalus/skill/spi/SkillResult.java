package tech.kayys.andalus.skill.spi;

import java.util.Map;

public interface SkillResult {

    Map<String, Object> getOutputs();

    boolean isSuccess();

    default String getSkillId() {
        return "";
    }

    default String getInvocationId() {
        return "";
    }

    default String getObservation() {
        return "";
    }

    default Map<String, Object> getMemoryUpdates() {
        return Map.of();
    }

    default Throwable getError() {
        return null;
    }

    static SkillResult success(String skillId, String observation) {
        return DefaultSkillResult.builder()
                .skillId(skillId)
                .observation(observation)
                .success(true)
                .build();
    }

    static SkillResult success(Map<String, Object> outputs) {
        return DefaultSkillResult.builder()
                .outputs(outputs)
                .success(true)
                .build();
    }

    static SkillResult failure(String skillId, String observation) {
        return DefaultSkillResult.builder()
                .skillId(skillId)
                .observation(observation)
                .success(false)
                .build();
    }

    static SkillResult failure(String skillId, Throwable error) {
        return DefaultSkillResult.builder()
                .skillId(skillId)
                .observation(error != null ? error.getMessage() : "Skill execution failed")
                .error(error)
                .success(false)
                .build();
    }
}
