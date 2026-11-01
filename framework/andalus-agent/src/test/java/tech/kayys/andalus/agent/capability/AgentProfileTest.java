package tech.kayys.andalus.agent.capability;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AgentProfileTest {

    @Test
    void createsImmutableProfileAndMatchesCapabilityIdentifiers() {
        Set<AgentCapability> capabilities = new HashSet<>();
        capabilities.add(new AgentCapability("research.web-search", "Search the web"));
        Map<String, String> metadata = new HashMap<>();
        metadata.put("domain", "research");

        AgentProfile profile = new AgentProfile(
                "researcher", "Researcher", "Research agent", capabilities, metadata);

        capabilities.clear();
        metadata.put("domain", "mutated");

        assertTrue(profile.supports("research.web-search"));
        assertFalse(profile.supports("unknown"));
        assertEquals(1, profile.capabilities().size());
        assertEquals("research", profile.metadata().get("domain"));
        assertThrows(UnsupportedOperationException.class,
                () -> profile.capabilities().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> profile.metadata().put("new", "value"));
    }

    @Test
    void rejectsBlankIdentityAndCapabilityFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new AgentCapability(" ", "Search"));
        assertThrows(IllegalArgumentException.class,
                () -> new AgentCapability("research.search", " "));
        assertThrows(IllegalArgumentException.class,
                () -> new AgentProfile(" ", "Agent", "Description", Set.of(), Map.of()));
    }

    @Test
    void treatsNullAndBlankCapabilityQueriesAsUnsupported() {
        AgentProfile profile = new AgentProfile(
                "researcher", "Researcher", "Research agent", Set.of(), Map.of());

        assertFalse(profile.supports(null));
        assertFalse(profile.supports(" "));
    }
}
