package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.projection.SpecAlignmentProjection;
import tech.kayys.andalus.a2ui.andalus.support.RecordValues;
import tech.kayys.andalus.a2ui.andalus.support.RecordMaps;

import java.util.Map;

/**
 * One locally verifiable A2UI spec-alignment expectation.
 */
public record WayangA2uiSpecAlignmentRequirement(
        String id,
        String category,
        String title,
        boolean aligned,
        Map<String, Object> expected,
        Map<String, Object> actual,
        String message) {

    public WayangA2uiSpecAlignmentRequirement {
        id = required(id, "A2UI spec alignment requirement id");
        category = required(category, "A2UI spec alignment requirement category");
        title = required(title, "A2UI spec alignment requirement title");
        expected = RecordMaps.stringKeysNonNullValues(expected);
        actual = RecordMaps.stringKeysNonNullValues(actual);
        message = RecordValues.text(message);
    }

    public static WayangA2uiSpecAlignmentRequirement aligned(
            String id,
            String category,
            String title,
            Map<String, Object> expected,
            Map<String, Object> actual) {
        return new WayangA2uiSpecAlignmentRequirement(
                id,
                category,
                title,
                true,
                expected,
                actual,
                "");
    }

    public static WayangA2uiSpecAlignmentRequirement gap(
            String id,
            String category,
            String title,
            Map<String, Object> expected,
            Map<String, Object> actual,
            String message) {
        return new WayangA2uiSpecAlignmentRequirement(
                id,
                category,
                title,
                false,
                expected,
                actual,
                message);
    }

    public Map<String, Object> toMap() {
        return SpecAlignmentProjection.requirement(this);
    }

    private static String required(String value, String name) {
        String normalized = RecordValues.text(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
