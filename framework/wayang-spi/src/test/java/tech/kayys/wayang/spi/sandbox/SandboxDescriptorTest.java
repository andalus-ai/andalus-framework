package tech.kayys.wayang.spi.sandbox;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.extension.Version;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SandboxDescriptorTest {

    @Test
    void testDescriptorValidationAndImmutability() {
        SandboxDescriptor desc = new SandboxDescriptor(
                "sb-1",
                "Container Sandbox",
                "Isolated container execution",
                SandboxType.CONTAINER,
                Version.parse("1.0.0"),
                Set.of("network-isolation", "filesystem-isolation"),
                Map.of("engine", "podman")
        );

        assertEquals("sb-1", desc.id());
        assertEquals("Container Sandbox", desc.name());
        assertEquals(SandboxType.CONTAINER, desc.type());
        assertEquals(2, desc.features().size());
        assertEquals("podman", desc.attributes().get("engine"));

        assertThrows(IllegalArgumentException.class, () ->
                new SandboxDescriptor("", "name", "desc", SandboxType.PROCESS, Version.parse("1.0.0"), Set.of(), Map.of())
        );

        assertThrows(NullPointerException.class, () ->
                new SandboxDescriptor("id", "name", "desc", null, Version.parse("1.0.0"), Set.of(), Map.of())
        );
    }

    @Test
    void testProviderDescriptorCompatibility() {
        SandboxProviderDescriptor provider = new SandboxProviderDescriptor(
                "wayang.sandbox.container",
                "Container Provider",
                "Container-based execution isolation",
                Version.parse("1.0.0"),
                Set.of(SandboxType.CONTAINER),
                Set.of(IsolationFeature.FILESYSTEM_ISOLATION, IsolationFeature.NETWORK_ISOLATION, IsolationFeature.MEMORY_LIMITS),
                Map.of()
        );

        assertTrue(provider.supports(SandboxType.CONTAINER));
        assertFalse(provider.supports(SandboxType.VM));

        assertTrue(provider.supports(IsolationFeature.FILESYSTEM_ISOLATION));
        assertTrue(provider.supportsAll(Set.of(IsolationFeature.FILESYSTEM_ISOLATION, IsolationFeature.NETWORK_ISOLATION)));
        assertFalse(provider.supportsAll(Set.of(IsolationFeature.FILESYSTEM_ISOLATION, IsolationFeature.SECCOMP)));
    }
}
