package tech.kayys.andalus.skill.composition;

import java.util.Map;
import java.util.Optional;

/**
 * Immutable carrier for data exchanged between steps in a composite skill.
 */
public record SkillData(
        Map<String, Object> values
) {

    public SkillData {
        values = values == null ? Map.of() : Map.copyOf(values);
    }

    public static SkillData empty() {
        return new SkillData(Map.of());
    }

    public static SkillData of(Map<String, Object> values) {
        return new SkillData(values);
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(values.get(key));
    }
}
