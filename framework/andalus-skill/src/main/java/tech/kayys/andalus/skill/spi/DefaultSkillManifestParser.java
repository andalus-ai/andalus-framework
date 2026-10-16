package tech.kayys.andalus.skill.spi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DefaultSkillManifestParser implements SkillManifestParser {

    @Override
    public SkillManifest parse(Map<String, Object> manifest) {
        if (manifest == null || manifest.isEmpty()) {
            throw new IllegalArgumentException("Skill manifest must not be empty");
        }

        return new SkillManifest(
                string(manifest, "id"),
                string(manifest, "version"),
                string(manifest, "name"),
                optionalString(manifest, "description"),
                kind(manifest),
                optionalString(manifest, "category"),
                stringSet(manifest, "capabilities"),
                references(manifest, "skills"),
                references(manifest, "tools"),
                stringSet(manifest, "dependencies"),
                objectMap(manifest, "configuration"),
                objectMap(manifest, "metadata")
        );
    }

    @Override
    public Map<String, Object> serialize(SkillManifest manifest) {
        if (manifest == null) {
            return Map.of();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", manifest.id());
        result.put("version", manifest.version());
        result.put("name", manifest.name());
        result.put("description", manifest.description());
        result.put("kind", manifest.kind().name());
        result.put("category", manifest.category());

        result.put("capabilities", List.copyOf(manifest.capabilities()));

        result.put("skills", manifest.skills().stream()
                .map(SkillReference::id)
                .toList());

        result.put("tools", manifest.tools().stream()
                .map(SkillReference::id)
                .toList());

        result.put("dependencies", List.copyOf(manifest.dependencies()));
        result.put("configuration", manifest.configuration());
        result.put("metadata", manifest.metadata());

        return Map.copyOf(result);
    }

    private static String string(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("Manifest field '" + key + "' must be a non-empty string");
        }
        return text.trim();
    }

    private static String optionalString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static SkillKind kind(Map<String, Object> map) {
        String value = optionalString(map, "kind");
        if (value.isBlank()) {
            return SkillKind.INSTRUCTION;
        }
        return SkillKind.valueOf(value.trim().toUpperCase());
    }

    private static Set<String> stringSet(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return Set.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException("'" + key + "' must be an array");
        }
        return list.stream()
                .map(String::valueOf)
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static List<SkillReference> references(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException("'" + key + "' must be an array");
        }
        return list.stream()
                .map(String::valueOf)
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .map(SkillReference::of)
                .toList();
    }

    private static Map<String, Object> objectMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return Map.of();
        }
        if (!(value instanceof Map<?, ?> raw)) {
            throw new IllegalArgumentException("'" + key + "' must be an object");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        raw.forEach((k, v) -> result.put(String.valueOf(k), v));
        return Map.copyOf(result);
    }
}
