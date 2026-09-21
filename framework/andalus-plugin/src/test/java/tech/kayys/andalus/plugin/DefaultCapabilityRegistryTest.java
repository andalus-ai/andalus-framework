package tech.kayys.andalus.plugin;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.spi.capability.Capability;
import tech.kayys.andalus.spi.capability.CapabilityDescriptor;
import tech.kayys.andalus.spi.capability.CapabilityProviderRegistration;
import tech.kayys.andalus.spi.capability.CapabilityType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCapabilityRegistryTest {

    @Test
    void registersAndFindsCapability() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        Capability capability = mockCapability("andalus.browser", "browser-local");

        registry.register("andalus.browser", "browser-local", capability);

        assertTrue(registry.contains("andalus.browser"));
        assertEquals(1, registry.providersOf("andalus.browser").size());

        Optional<Capability> found = registry.find("andalus.browser");
        assertTrue(found.isPresent());
        assertEquals("andalus.browser", found.get().id());
    }

    @Test
    void allowsMultipleProvidersForSameCapability() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        Capability local = mockCapability("andalus.ocr", "ocr-local");
        Capability remote = mockCapability("andalus.ocr", "ocr-cloud");

        registry.register("andalus.ocr", "ocr-local", local);
        registry.register("andalus.ocr", "ocr-cloud", remote);

        List<CapabilityProviderRegistration> providers = registry.providersOf("andalus.ocr");
        assertEquals(2, providers.size());
    }

    @Test
    void rejectsDuplicateRegistrationForSameCapabilityAndProvider() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        Capability c1 = mockCapability("andalus.git", "git-core");
        Capability c2 = mockCapability("andalus.git", "git-core");

        registry.register("andalus.git", "git-core", c1);

        assertThrows(IllegalStateException.class, () ->
                registry.register("andalus.git", "git-core", c2));
    }

    @Test
    void unregistersSpecificProvider() {
        DefaultCapabilityRegistry registry = new DefaultCapabilityRegistry();
        Capability c1 = mockCapability("andalus.git", "provider-a");
        Capability c2 = mockCapability("andalus.git", "provider-b");

        registry.register("andalus.git", "provider-a", c1);
        registry.register("andalus.git", "provider-b", c2);

        registry.unregister("andalus.git", "provider-a");

        assertEquals(1, registry.providersOf("andalus.git").size());
        assertEquals("provider-b", registry.providersOf("andalus.git").getFirst().providerId());
    }

    private Capability mockCapability(String id, String providerId) {
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
                return new CapabilityDescriptor(id, "Test capability", type(), Map.of());
            }
        };
    }
}
