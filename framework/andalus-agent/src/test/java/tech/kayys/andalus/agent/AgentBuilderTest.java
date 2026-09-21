package tech.kayys.andalus.agent;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.agent.builder.AgentBuilder;
import tech.kayys.andalus.agent.react.ReActAgent;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentBuilderTest {

    @Test
    public void testBuildReActAgent() {
        Agent agent = AgentBuilder.create("react")
                .withSystemPrompt("You are a test agent")
                .build();

        assertNotNull(agent);
        assertTrue(agent instanceof ReActAgent, "Agent should be a ReActAgent");
    }
}
