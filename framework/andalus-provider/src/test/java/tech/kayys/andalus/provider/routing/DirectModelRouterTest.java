package tech.kayys.andalus.provider.routing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.agent.AgentRequest;
import tech.kayys.andalus.core.AgentDefinition;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.extension.Reference;
import tech.kayys.andalus.provider.*;
import tech.kayys.andalus.resource.Modality;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class DirectModelRouterTest {

    private static class MockProvider implements Provider {
        private final String id;

        public MockProvider(String id) {
            this.id = id;
        }

        @Override
        public String id() { return id; }

        @Override
        public void streamChat(List<ChatMessage> messages, String systemPrompt, List<ToolSpec> tools, double temperature, int maxTokens, Consumer<StreamEvent> onEvent) throws IOException, InterruptedException { }
    }

    private DirectModelRouter directRouter;
    private Provider openaiProvider;
    private Provider anthropicProvider;
    private List<Provider> availableProviders;

    @BeforeEach
    void setUp() {
        directRouter = new DirectModelRouter();
        openaiProvider = new MockProvider("openai");
        anthropicProvider = new MockProvider("anthropic");
        availableProviders = List.of(openaiProvider, anthropicProvider);
    }

    @Test
    void testDirectRoutingHonorsExplicitRequestedModel() {
        AgentDefinition agentDef = AgentDefinition.builder()
                .metadata(Metadata.builder().name("TestAgent").build())
                .model(Reference.of(Id.random(), tech.kayys.andalus.resource.ResourceType.fromString("model"), "anthropic"))
                .build();

        InferencePlan plan = directRouter.plan(null, agentDef, InferenceRequirements.defaults(), InferencePolicy.defaults(), availableProviders);

        assertNotNull(plan);
        assertEquals("anthropic", plan.selectedModel());
        assertEquals("anthropic", plan.selectedProvider().id());
        assertTrue(plan.decisionReason().contains("Direct explicit match"));
    }

    @Test
    void testDirectRoutingDefaultsToFirstProviderIfNoModelSpecified() {
        AgentDefinition agentDef = AgentDefinition.builder()
                .metadata(Metadata.builder().name("DefaultAgent").build())
                .build();

        InferencePlan plan = directRouter.plan(null, agentDef, InferenceRequirements.defaults(), InferencePolicy.defaults(), availableProviders);

        assertNotNull(plan);
        assertEquals("openai", plan.selectedProvider().id());
        assertTrue(plan.decisionReason().contains("defaulting to first available provider"));
    }

    @Test
    void testConfigurableDefaultModelRouterSwitchesStrategy() {
        DefaultModelRouter directMode = new DefaultModelRouter(RoutingStrategy.DIRECT);
        assertEquals(RoutingStrategy.DIRECT, directMode.getStrategy());

        DefaultModelRouter adaptiveMode = new DefaultModelRouter(RoutingStrategy.ADAPTIVE);
        assertEquals(RoutingStrategy.ADAPTIVE, adaptiveMode.getStrategy());
    }
}
