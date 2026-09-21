package tech.kayys.andalus.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



/**
 * SDK-local registry of standards that Andalus can align and report against.
 */
public final class AndalusStandardRegistry {

    public static final String A2A = "a2a";
    public static final String A2UI = "a2ui";
    public static final String AGENTIC_COMMERCE = "agentic-commerce";

    private static final List<AndalusStandardDefinition> DEFAULT_STANDARDS = List.of(
            new AndalusStandardDefinition(
                    A2A,
                    "Agent2Agent Protocol",
                    "1.0",
                    "JSONRPC",
                    "https://a2a-protocol.org/latest/specification/",
                    List.of("agent2agent", "agent-to-agent", "agent2agent-protocol", "a2a-jsonrpc"),
                    Map.of()),
            new AndalusStandardDefinition(
                    A2UI,
                    "Agent-to-User Interface",
                    "v0.8",
                    "HTTP",
                    "https://a2ui.org/specification/v0_8/standard_catalog_definition.json",
                    List.of("agent-to-user-interface", "agent-user-interface", "a2ui-v0.8"),
                    Map.of("extensionUri", "https://a2ui.org/a2a-extension/a2ui/v0.8")),
            new AndalusStandardDefinition(
                    AGENTIC_COMMERCE,
                    "Agentic Commerce Protocol",
                    "2026-01-30",
                    "HTTP+JSON",
                    "https://www.agenticcommerce.dev",
                    List.of("agenticcommerce", "agentic-commerce-protocol", "agentic-commerce-dev"),
                    Map.of("specHome", "https://www.agenticcommerce.dev")));
    private static final Map<String, AndalusStandardDefinition> BY_KEY = index(DEFAULT_STANDARDS);

    private AndalusStandardRegistry() {
    }

    public static List<AndalusStandardDefinition> knownStandards() {
        return DEFAULT_STANDARDS;
    }

    public static Optional<AndalusStandardDefinition> find(String idOrAlias) {
        return Optional.ofNullable(BY_KEY.get(normalizeKey(idOrAlias)));
    }

    public static boolean known(String idOrAlias) {
        return find(idOrAlias).isPresent();
    }

    public static AndalusStandardRegistryDriftReport driftReport(AndalusStandardAlignmentPortfolio portfolio) {
        return AndalusStandardRegistryDriftReport.from(portfolio);
    }

    public static String canonicalId(String idOrAlias) {
        String normalized = SdkText.trimToEmpty(idOrAlias);
        return find(normalized)
                .map(AndalusStandardDefinition::standardId)
                .orElse(normalized);
    }

    public static AndalusStandardAlignmentDescriptor enrich(AndalusStandardAlignmentDescriptor descriptor) {
        if (descriptor == null) {
            return AndalusStandardAlignmentDescriptor.fromReportMap(Map.of());
        }
        return find(descriptor.standardId())
                .map(definition -> enrich(descriptor, definition))
                .orElse(descriptor);
    }

    private static AndalusStandardAlignmentDescriptor enrich(
            AndalusStandardAlignmentDescriptor descriptor,
            AndalusStandardDefinition definition) {
        Map<String, Object> attributes = new LinkedHashMap<>(definition.attributes());
        attributes.putAll(descriptor.attributes());
        return new AndalusStandardAlignmentDescriptor(
                definition.standardId(),
                defaulted(descriptor.name(), descriptor.standardId(), definition.name()),
                defaulted(descriptor.version(), "", definition.version()),
                defaulted(descriptor.binding(), "", definition.binding()),
                defaulted(descriptor.specUrl(), "", definition.specUrl()),
                attributes);
    }

    private static String defaulted(String actual, String emptyValue, String fallback) {
        String normalized = SdkText.trimToEmpty(actual);
        return normalized.isEmpty() || normalized.equals(emptyValue) ? fallback : normalized;
    }

    private static Map<String, AndalusStandardDefinition> index(List<AndalusStandardDefinition> standards) {
        Map<String, AndalusStandardDefinition> values = new LinkedHashMap<>();
        standards.forEach(definition -> {
            values.put(normalizeKey(definition.standardId()), definition);
            values.put(normalizeKey(definition.name()), definition);
            definition.aliases().forEach(alias -> values.put(normalizeKey(alias), definition));
        });
        return SdkMaps.orderedTypedCopy(values);
    }

    private static String normalizeKey(String value) {
        String normalized = SdkText.trimToEmpty(value).toLowerCase();
        if (normalized.isEmpty()) {
            return "";
        }
        return normalized.replaceAll("[^a-z0-9]+", "");
    }
}
