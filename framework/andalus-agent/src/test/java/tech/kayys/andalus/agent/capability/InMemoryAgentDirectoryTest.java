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
