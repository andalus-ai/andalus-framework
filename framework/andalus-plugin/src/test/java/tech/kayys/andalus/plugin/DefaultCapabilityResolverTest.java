package tech.kayys.andalus.plugin;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.spi.capability.Capability;
import tech.kayys.andalus.spi.capability.CapabilityDescriptor;
import tech.kayys.andalus.spi.capability.CapabilityRequirement;
import tech.kayys.andalus.spi.capability.CapabilityRequirements;
import tech.kayys.andalus.spi.capability.CapabilityResolutionResult;
import tech.kayys.andalus.spi.capability.CapabilityType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCapabilityResolverTest {

    @Test
    void resolvesRequiredCapabilities() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        registry.register("andalus.browser", "default", mockCapability("andalus.browser", Map.of()));
        registry.register("andalus.git", "default", mockCapability("andalus.git", Map.of()));

        DefaultCapabilityResolver resolver = new DefaultCapabilityResolver(registry);

        CapabilityRequirements reqs = new CapabilityRequirements(List.of(
                CapabilityRequirement.required("andalus.browser"),
                CapabilityRequirement.required("andalus.git")
        ));

        CapabilityResolutionResult result = resolver.resolve(reqs);

        assertTrue(result.satisfied());
        assertEquals(2, result.resolved().size());
    }

    @Test
    void failsWhenRequiredCapabilityIsMissing() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        registry.register("andalus.browser", "default", mockCapability("andalus.browser", Map.of()));

        DefaultCapabilityResolver resolver = new DefaultCapabilityResolver(registry);

        CapabilityRequirements reqs = new CapabilityRequirements(List.of(
                CapabilityRequirement.required("andalus.browser"),
                CapabilityRequirement.required("andalus.missing")
        ));

        CapabilityResolutionResult result = resolver.resolve(reqs);

        assertFalse(result.satisfied());
        assertTrue(result.missing().contains("andalus.missing"));
    }

    @Test
    void succeedsWhenOptionalCapabilityIsMissing() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        registry.register("andalus.browser", "default", mockCapability("andalus.browser", Map.of()));

        DefaultCapabilityResolver resolver = new DefaultCapabilityResolver(registry);

        CapabilityRequirements reqs = new CapabilityRequirements(List.of(
                CapabilityRequirement.required("andalus.browser"),
                CapabilityRequirement.optional("andalus.optional-feature")
        ));

        CapabilityResolutionResult result = resolver.resolve(reqs);

        assertTrue(result.satisfied());
        assertEquals(1, result.resolved().size());
    }

    private Capability mockCapability(String id, Map<String, Object> attributes) {
        return new Capability() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public CapabilityType type() {
                return new CapabilityType("tool", "system");
            }

            @Override
            public CapabilityDescriptor descriptor() {
                return new CapabilityDescriptor(id, "Test", type(), attributes);
            }
        };
    }
}
