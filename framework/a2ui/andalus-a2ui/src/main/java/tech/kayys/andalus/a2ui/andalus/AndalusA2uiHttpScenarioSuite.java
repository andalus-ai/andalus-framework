package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;

import tech.kayys.andalus.a2ui.andalus.support.RecordValues;
import tech.kayys.andalus.a2ui.andalus.support.RecordCollections;

import java.util.List;
import java.util.Map;

/**
 * Named collection of HTTP scenarios for grouped smoke and diagnostic runs.
 */
public record WayangA2uiHttpScenarioSuite(
        String id,
        List<WayangA2uiHttpScenario> scenarios,
        Map<String, Object> attributes) {

    public WayangA2uiHttpScenarioSuite {
        id = RecordValues.textOrDefault(id, "a2ui-http-suite");
        scenarios = RecordCollections.nonNullList(scenarios);
        attributes = TransportMaps.copy(attributes);
    }

    public static WayangA2uiHttpScenarioSuite of(String id, WayangA2uiHttpScenario... scenarios) {
        return of(id, RecordCollections.nonNullVarargs(scenarios));
    }

    public static WayangA2uiHttpScenarioSuite of(String id, List<WayangA2uiHttpScenario> scenarios) {
        return new WayangA2uiHttpScenarioSuite(id, scenarios, Map.of());
    }

    public WayangA2uiHttpScenarioSuite withAttributes(Map<?, ?> extraAttributes) {
        if (extraAttributes == null || extraAttributes.isEmpty()) {
            return this;
        }
        return new WayangA2uiHttpScenarioSuite(
                id,
                scenarios,
                WayangA2uiTransportMetadata.merge(attributes, TransportMaps.copy(extraAttributes)));
    }

    public boolean empty() {
        return scenarios.isEmpty();
    }

    public int size() {
        return scenarios.size();
    }
}
