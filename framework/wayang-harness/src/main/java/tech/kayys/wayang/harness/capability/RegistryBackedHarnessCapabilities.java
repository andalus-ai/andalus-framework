package tech.kayys.wayang.harness.capability;

import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.harness.environment.HarnessCapabilities;
import tech.kayys.wayang.spi.capability.Capability;
import tech.kayys.wayang.spi.capability.CapabilityInvocationContext;
import tech.kayys.wayang.spi.capability.CapabilityInvocationResult;
import tech.kayys.wayang.spi.capability.CapabilityInvoker;
import tech.kayys.wayang.spi.capability.CapabilityRegistry;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Adapter exposing the platform {@link CapabilityRegistry} through the Harness capability governance boundary.
 */
public class RegistryBackedHarnessCapabilities implements HarnessCapabilities {

    private final CapabilityRegistry registry;
    private final CapabilityScope scope;
    private final Map<String, CapabilityInvoker> invokers = new ConcurrentHashMap<>();

    public RegistryBackedHarnessCapabilities(CapabilityRegistry registry, CapabilityScope scope) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.scope = Objects.requireNonNull(scope, "scope");
    }

    public void registerInvoker(String capabilityId, CapabilityInvoker invoker) {
        invokers.put(capabilityId, invoker);
    }

    @Override
    public boolean has(CapabilityId capability) {
        Objects.requireNonNull(capability, "capability");
        return available(capability.value());
    }

    @Override
    public void require(CapabilityId capability) {
        Objects.requireNonNull(capability, "capability");
        if (!has(capability)) {
            throw new NoSuchElementException("Required capability not available: " + capability.value());
        }
    }

    @Override
    public Set<CapabilityId> available() {
        return registry.findAll().stream()
                .map(Capability::id)
                .filter(scope::allows)
                .map(CapabilityId::of)
                .collect(Collectors.toSet());
    }

    public boolean available(String capabilityId) {
        return registry.contains(capabilityId) && scope.allows(capabilityId);
    }

    public CapabilityDecision evaluate(CapabilityRequest request) {
        Objects.requireNonNull(request, "request");
        if (!registry.contains(request.capabilityId())) {
            return CapabilityDecision.deny("Capability not registered: " + request.capabilityId());
        }
        return scope.evaluate(request);
    }

    public CapabilityInvocationResult invoke(CapabilityRequest request, CapabilityInvocationContext context) throws Exception {
        CapabilityDecision decision = evaluate(request);
        if (!decision.isAllowed()) {
            return CapabilityInvocationResult.failure(decision.reason());
        }

        CapabilityInvoker invoker = invokers.get(request.capabilityId());
        if (invoker == null) {
            return CapabilityInvocationResult.failure("No invoker configured for capability: " + request.capabilityId());
        }

        return invoker.invoke(context);
    }

    public CapabilityRegistry registry() {
        return registry;
    }

    public CapabilityScope scope() {
        return scope;
    }
}
