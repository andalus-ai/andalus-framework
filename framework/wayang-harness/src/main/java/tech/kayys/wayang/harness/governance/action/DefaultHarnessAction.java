package tech.kayys.wayang.harness.governance.action;

import tech.kayys.wayang.harness.environment.ResourceId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default immutable implementation of {@link HarnessAction}.
 */
public record DefaultHarnessAction(
        String id,
        String type,
        String capability,
        Optional<ResourceId> resource,
        Map<String, Object> arguments,
        ActionMetadata metadata
) implements HarnessAction {

    public DefaultHarnessAction {
        id = id == null ? "action-" + UUID.randomUUID() : id;
        type = Objects.requireNonNull(type, "type");
        capability = Objects.requireNonNull(capability, "capability");
        resource = resource == null ? Optional.empty() : resource;
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
        metadata = metadata == null ? ActionMetadata.readOnly() : metadata;
    }

    public static DefaultHarnessAction of(String type, String capability) {
        return new DefaultHarnessAction(null, type, capability, Optional.empty(), Map.of(), ActionMetadata.readOnly());
    }

    public static DefaultHarnessAction of(String type, String capability, ResourceId resource) {
        return new DefaultHarnessAction(null, type, capability, Optional.ofNullable(resource), Map.of(), ActionMetadata.readOnly());
    }

    public static DefaultHarnessAction of(String type, String capability, ResourceId resource, Map<String, Object> args, ActionMetadata metadata) {
        return new DefaultHarnessAction(null, type, capability, Optional.ofNullable(resource), args, metadata);
    }
}
