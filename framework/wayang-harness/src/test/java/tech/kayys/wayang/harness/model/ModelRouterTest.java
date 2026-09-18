package tech.kayys.wayang.harness.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ModelRouterTest {

    private ModelCatalog catalog;
    private ModelDescriptor localQwen;
    private ModelDescriptor cloudClaude;

    @BeforeEach
    void setUp() {
        catalog = ModelCatalog.create();
        localQwen = DefaultModelDescriptor.builder(ModelId.of("local-qwen"), "Qwen-Local")
                .tasks(Set.of(ModelTask.CHAT, ModelTask.REASONING))
                .metadata(ModelMetadata.local("qwen"))
                .limits(ModelLimits.of(32000, 2048))
                .build();

        cloudClaude = DefaultModelDescriptor.builder(ModelId.of("cloud-claude"), "Claude-Cloud")
                .tasks(Set.of(ModelTask.CHAT, ModelTask.REASONING))
                .metadata(ModelMetadata.cloud("anthropic"))
                .limits(ModelLimits.of(200000, 8192))
                .build();

        ModelProvider provider = new ModelProvider() {
            @Override
            public Collection<ModelDescriptor> models() {
                return List.of(localQwen, cloudClaude);
            }

            @Override
            public Optional<ModelDescriptor> describe(ModelId modelId) {
                if (modelId.equals(localQwen.id())) return Optional.of(localQwen);
                if (modelId.equals(cloudClaude.id())) return Optional.of(cloudClaude);
                return Optional.empty();
            }

            @Override
            public ModelExecutor executor(ModelId modelId) {
                return (inv, ctx) -> ModelResult.success(inv.id(), "hello", ModelUsage.empty(), ModelMetadata.local("test"));
            }
        };
        catalog.register(provider);
    }

    @Test
    void shouldSelectLocalModelFirstWhenApplicable() {
        ModelRouter router = new DefaultModelRouter(catalog, ModelRoutingPolicy.localFirst());
        ModelIntent intent = ModelIntent.prompt("What is 2+2?");

        Optional<ModelResolution> res = router.resolve(intent, ModelRoutingContext.of(null, null));
        assertThat(res).isPresent();
        assertThat(res.get().model().id()).isEqualTo(ModelId.of("local-qwen"));
        assertThat(res.get().reason().strategy()).isEqualTo("LOCAL_FIRST");
    }

    @Test
    void shouldFallbackToCloudWhenContextExceedsLocalLimits() {
        ModelRouter router = new DefaultModelRouter(catalog, ModelRoutingPolicy.localFirst());
        // Require 100,000 context tokens: localQwen (32k) cannot handle this, so it must pick cloudClaude (200k)
        ModelRequirements req = new ModelRequirements(ModelTask.CHAT, true, false, false, 100000, Optional.empty());
        ModelIntent intent = new ModelIntent(ModelIntentId.generate(), ModelTask.CHAT, req, ModelInput.of("large doc"), null);

        Optional<ModelResolution> res = router.resolve(intent, ModelRoutingContext.of(null, null));
        assertThat(res).isPresent();
        assertThat(res.get().model().id()).isEqualTo(ModelId.of("cloud-claude"));
        assertThat(res.get().reason().strategy()).isEqualTo("CLOUD_FALLBACK");
    }
}
