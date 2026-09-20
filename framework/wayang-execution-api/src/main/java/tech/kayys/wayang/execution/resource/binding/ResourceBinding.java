package tech.kayys.wayang.execution.resource.binding;

import tech.kayys.wayang.execution.resource.model.ResourceId;

import java.util.Map;
import java.util.Objects;

/**
 * Concrete binding of a logical VirtualResource to physical infrastructure or handle.
 */
public record ResourceBinding(
        ResourceId resourceId,
        String physicalId,
        ResourceBindingState state,
        Map<String, Object> handleDetails
) {
    public ResourceBinding {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(physicalId, "physicalId cannot be null");
        if (state == null) {
            state = ResourceBindingState.BOUND;
        }
        handleDetails = handleDetails == null ? Map.of() : Map.copyOf(handleDetails);
    }

    public static ResourceBinding bound(ResourceId resourceId, String physicalId) {
        return new ResourceBinding(resourceId, physicalId, ResourceBindingState.BOUND, Map.of());
    }

    public static ResourceBinding active(ResourceId resourceId, String physicalId, Map<String, Object> details) {
        return new ResourceBinding(resourceId, physicalId, ResourceBindingState.ACTIVE, details);
    }

    public ResourceBinding withState(ResourceBindingState newState) {
        return new ResourceBinding(resourceId, physicalId, newState, handleDetails);
    }
}
