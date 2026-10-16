package tech.kayys.andalus.a2ui.andalus.session;

import tech.kayys.andalus.a2ui.andalus.WayangA2uiActionPolicy;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSessionConfig;
import tech.kayys.andalus.a2ui.andalus.support.DecodeCollections;
import tech.kayys.andalus.a2ui.andalus.support.DecodeValues;
import tech.kayys.andalus.a2ui.andalus.support.StringMaps;
import tech.kayys.andalus.a2ui.andalus.transport.TransportJson;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Decodes stored or remote A2UI session configuration into public session contracts.
 */
public final class SessionConfigDecoder {

    public static WayangA2uiSessionConfig fromMap(Map<?, ?> values) {
        if (values == null || values.isEmpty()) {
            return WayangA2uiSessionConfig.defaultConfig();
        }
        Map<String, Object> config = new LinkedHashMap<>(TransportMaps.copy(values));
        boolean enabled = DecodeValues.bool(config.get(WayangA2uiSessionConfig.KEY_ENABLED), true);
        Map<String, Object> policy = policyMap(config);
        return new WayangA2uiSessionConfig(enabled, policy(policy));
    }

    public static WayangA2uiSessionConfig fromJson(String json) {
        return fromMap(TransportJson.map(
                json,
                "A2UI session config JSON must not be blank",
                "Unable to decode A2UI session config JSON"));
    }

    private static WayangA2uiActionPolicy policy(Map<String, Object> values) {
        String mode = DecodeValues.text(values.get(WayangA2uiSessionConfig.KEY_MODE));
        WayangA2uiActionPolicy base = SessionProfiles.actionPolicy(mode);
        Set<String> allowedActions = DecodeCollections.commaSeparatedTextSet(
                values.get(WayangA2uiSessionConfig.KEY_ALLOWED_ACTIONS));
        Set<String> allowedRunIds = DecodeCollections.commaSeparatedTextSet(
                values.get(WayangA2uiSessionConfig.KEY_ALLOWED_RUN_IDS));
        Map<String, String> requiredContext = StringMaps.stringValues(
                values.get(WayangA2uiSessionConfig.KEY_REQUIRED_CONTEXT));
        return new WayangA2uiActionPolicy(
                allowedActions.isEmpty() ? base.allowedActions() : allowedActions,
                allowedRunIds.isEmpty() ? base.allowedRunIds() : allowedRunIds,
                requiredContext.isEmpty() ? base.requiredContext() : requiredContext);
    }

    private static Map<String, Object> policyMap(Map<String, Object> config) {
        Object nested = config.get(WayangA2uiSessionConfig.KEY_POLICY);
        if (nested instanceof Map<?, ?> nestedMap) {
            Map<String, Object> copy = new LinkedHashMap<>(TransportMaps.copy(nestedMap));
            config.forEach((key, value) -> {
                if (!WayangA2uiSessionConfig.KEY_POLICY.equals(key)
                        && !WayangA2uiSessionConfig.KEY_ENABLED.equals(key)) {
                    copy.putIfAbsent(key, value);
                }
            });
            return copy;
        }
        Map<String, Object> policy = new LinkedHashMap<>(config);
        policy.remove(WayangA2uiSessionConfig.KEY_ENABLED);
        return policy;
    }

    private SessionConfigDecoder() {
    }
}
