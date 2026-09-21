package tech.kayys.andalus.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Registry drift report for standard-alignment descriptors.
 */
public record AndalusStandardRegistryDriftReport(
        List<String> checkedStandardIds,
        List<String> unknownStandardIds,
        List<AndalusStandardRegistryDriftIssue> issues) {

    public AndalusStandardRegistryDriftReport {
        checkedStandardIds = copyStrings(checkedStandardIds);
        unknownStandardIds = copyStrings(unknownStandardIds);
        issues = issues == null
                ? List.of()
                : issues.stream()
                        .filter(issue -> issue != null)
                        .toList();
    }

    public static AndalusStandardRegistryDriftReport from(AndalusStandardAlignmentPortfolio portfolio) {
        AndalusStandardAlignmentPortfolio resolved = portfolio == null
                ? AndalusStandardAlignmentPortfolio.builder().build()
                : portfolio;
        List<String> checked = new ArrayList<>();
        List<String> unknown = new ArrayList<>();
        List<AndalusStandardRegistryDriftIssue> issues = new ArrayList<>();
        resolved.standards().forEach(summary ->
                AndalusStandardRegistry.find(summary.standardId())
                        .ifPresentOrElse(
                                definition -> {
                                    checked.add(definition.standardId());
                                    issues.addAll(compare(summary.standard(), definition));
                                },
                                () -> unknown.add(summary.standardId())));
        return new AndalusStandardRegistryDriftReport(checked, unknown, issues);
    }

    public boolean driftFree() {
        return issues.isEmpty();
    }

    public boolean hasDrift() {
        return !driftFree();
    }

    public boolean hasUnknownStandards() {
        return !unknownStandardIds.isEmpty();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("driftFree", driftFree());
        values.put("checkedStandardIds", checkedStandardIds);
        values.put("unknownStandardIds", unknownStandardIds);
        values.put("issues", issues.stream()
                .map(AndalusStandardRegistryDriftIssue::toMap)
                .toList());
        return SdkMaps.orderedCopy(values);
    }

    private static List<AndalusStandardRegistryDriftIssue> compare(
            AndalusStandardAlignmentDescriptor descriptor,
            AndalusStandardDefinition definition) {
        List<AndalusStandardRegistryDriftIssue> issues = new ArrayList<>();
        compare(issues, definition.standardId(), "name", definition.name(), descriptor.name());
        compare(issues, definition.standardId(), "version", definition.version(), descriptor.version());
        compare(issues, definition.standardId(), "binding", definition.binding(), descriptor.binding());
        compare(issues, definition.standardId(), "specUrl", definition.specUrl(), descriptor.specUrl());
        definition.attributes().forEach((field, expected) ->
                compare(
                        issues,
                        definition.standardId(),
                        field,
                        text(expected),
                        text(descriptor.attributes().get(field))));
        return issues;
    }

    private static void compare(
            List<AndalusStandardRegistryDriftIssue> issues,
            String standardId,
            String field,
            String expected,
            String actual) {
        if (!expected.isEmpty() && !expected.equals(actual)) {
            issues.add(new AndalusStandardRegistryDriftIssue(standardId, field, expected, actual));
        }
    }

    private static List<String> copyStrings(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        Set<String> copy = new LinkedHashSet<>();
        values.stream()
                .map(SdkText::trimToEmpty)
                .filter(value -> !value.isEmpty())
                .forEach(copy::add);
        return copy.isEmpty() ? List.of() : List.copyOf(copy);
    }

    private static String text(Object value) {
        return SdkText.trimToEmpty(value == null ? "" : String.valueOf(value));
    }
}
