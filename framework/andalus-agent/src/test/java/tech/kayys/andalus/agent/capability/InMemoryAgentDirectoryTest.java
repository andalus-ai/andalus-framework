package tech.kayys.andalus.agent.capability;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAgentDirectoryTest {

    @Test
    void registersAndFindsAgentsByIdAndCapabilityInStableOrder() {
        AgentDirectory directory = new InMemoryAgentDirectory();
        AgentCapability search = new AgentCapability("research.search", "Search sources");

        directory.register(new AgentProfile("zeta", "Zeta", "Second agent",
                Set.of(search), Map.of()));
        directory.register(new AgentProfile("alpha", "Alpha", "First agent",
                Set.of(search), Map.of()));

        assertEquals("alpha", directory.findById(" alpha ").orElseThrow().id());
        assertEquals(
                java.util.List.of("alpha", "zeta"),
                directory.findByCapability("research.search").stream()
                        .map(AgentProfile::id)
                        .toList());
        assertEquals(java.util.List.of("alpha", "zeta"),
                directory.findAll().stream().map(AgentProfile::id).toList());
    }

    @Test
    void findsAgentsThatSupportEveryRequestedCapabilityInStableOrder() {
        AgentDirectory directory = new InMemoryAgentDirectory();
        AgentCapability search = new AgentCapability("research.search", "Search sources");
        AgentCapability summarize = new AgentCapability("research.summarize", "Summarize sources");
        AgentCapability translate = new AgentCapability("language.translate", "Translate text");

        directory.register(new AgentProfile("zeta", "Zeta", "Search and summarize",
                Set.of(search, summarize), Map.of()));
        directory.register(new AgentProfile("alpha", "Alpha", "Search, summarize and translate",
                Set.of(search, summarize, translate), Map.of()));
        directory.register(new AgentProfile("beta", "Beta", "Search only",
                Set.of(search), Map.of()));

        assertEquals(
                java.util.List.of("alpha", "zeta"),
                directory.findByCapabilities(Set.of(" research.search ", "research.summarize"))
                        .stream()
                        .map(AgentProfile::id)
                        .toList());
    }

    @Test
    void multiCapabilityLookupDoesNotTreatInvalidQueriesAsMatchAll() {
        AgentDirectory directory = new InMemoryAgentDirectory();
        directory.register(new AgentProfile("agent", "Agent", "Example agent", Set.of(), Map.of()));

        assertTrue(directory.findByCapabilities(null).isEmpty());
        assertTrue(directory.findByCapabilities(Set.of()).isEmpty());
        assertTrue(directory.findByCapabilities(java.util.Collections.singleton(null)).isEmpty());
        assertTrue(directory.findByCapabilities(Set.of(" ")).isEmpty());
    }

    @Test
    void tenantVisibilityIncludesOnlyPublicAndMatchingTenantProfiles() {
        AgentDirectory directory = new InMemoryAgentDirectory();

        directory.register(new AgentProfile("public-agent", "Public", "Public agent",
                Set.of(), Map.of(AgentDirectory.VISIBILITY_METADATA_KEY, AgentDirectory.PUBLIC_VISIBILITY)));
        directory.register(new AgentProfile("tenant-a-agent", "Tenant A", "Tenant A agent",
                Set.of(), Map.of(AgentDirectory.TENANT_ID_METADATA_KEY, "tenant-a")));
        directory.register(new AgentProfile("tenant-b-agent", "Tenant B", "Tenant B agent",
                Set.of(), Map.of(AgentDirectory.TENANT_ID_METADATA_KEY, "tenant-b")));
        directory.register(new AgentProfile("unscoped-agent", "Unscoped", "No visibility metadata",
                Set.of(), Map.of()));

        assertEquals(java.util.List.of("public-agent", "tenant-a-agent"),
                directory.findVisibleToTenant(" tenant-a ").stream()
                        .map(AgentProfile::id).toList());
        assertEquals(java.util.List.of("public-agent"),
                directory.findVisibleToTenant("tenant-c").stream()
                        .map(AgentProfile::id).toList());
    }

    @Test
    void missingOrBlankTenantSeesOnlyExplicitlyPublicProfiles() {
        AgentDirectory directory = new InMemoryAgentDirectory();
        directory.register(new AgentProfile("public-agent", "Public", "Public agent",
                Set.of(), Map.of(AgentDirectory.VISIBILITY_METADATA_KEY, AgentDirectory.PUBLIC_VISIBILITY)));
        directory.register(new AgentProfile("tenant-agent", "Tenant", "Tenant agent",
                Set.of(), Map.of(AgentDirectory.TENANT_ID_METADATA_KEY, "tenant-a")));

        assertEquals(java.util.List.of("public-agent"),
                directory.findVisibleToTenant(null).stream().map(AgentProfile::id).toList());
        assertEquals(java.util.List.of("public-agent"),
                directory.findVisibleToTenant(" ").stream().map(AgentProfile::id).toList());
    }

    @Test
    void replacesProfilesAndUnregistersById() {
        AgentDirectory directory = new InMemoryAgentDirectory();
        directory.register(new AgentProfile("agent", "Old", "Old profile", Set.of(), Map.of()));
        directory.register(new AgentProfile("agent", "New", "New profile", Set.of(), Map.of()));

        assertEquals("New", directory.findById("agent").orElseThrow().name());
        assertTrue(directory.unregister("agent"));
        assertFalse(directory.unregister("agent"));
        assertTrue(directory.findAll().isEmpty());
    }

    @Test
    void handlesInvalidLookupInputsWithoutThrowing() {
        AgentDirectory directory = new InMemoryAgentDirectory();

        assertTrue(directory.findById(null).isEmpty());
        assertTrue(directory.findById(" ").isEmpty());
        assertTrue(directory.findByCapability(null).isEmpty());
        assertFalse(directory.unregister(null));
    }
}
