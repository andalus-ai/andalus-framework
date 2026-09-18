package tech.kayys.wayang.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.catalog.ToolCatalog;
import tech.kayys.wayang.tool.catalog.ToolDiscoveryRequest;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolCatalogTest {

    private ToolCatalog catalog;
    private ToolProvider dummyProvider;
    private ToolDescriptor toolDescriptor;

    @BeforeEach
    void setUp() {
        catalog = ToolCatalog.create();
        toolDescriptor = DefaultToolDescriptor.builder(ToolId.of("test-fs"), "FileSystem")
                .capabilities(Set.of("filesystem.read", "filesystem.write"))
                .build();

        dummyProvider = new ToolProvider() {
            @Override
            public Optional<ToolDescriptor> describe(ToolId id) {
                return id.equals(toolDescriptor.idAsToolId()) ? Optional.of(toolDescriptor) : Optional.empty();
            }

            @Override
            public Collection<ToolDescriptor> tools() {
                return Set.of(toolDescriptor);
            }

            @Override
            public ToolExecutor executor(ToolId id) {
                return ToolExecutor.synchronous((inv, ctx) -> ToolResult.success(inv.invocationIdentifier(), "ok", Duration.ZERO, "dummy"));
            }
        };
    }

    @Test
    void shouldDiscoverToolsByCapability() {
        catalog.register(dummyProvider);

        Collection<ToolDescriptor> found = catalog.discover(ToolDiscoveryRequest.forCapability("filesystem.read"));
        assertThat(found).hasSize(1);
        assertThat(found.iterator().next().name()).isEqualTo("FileSystem");

        Collection<ToolDescriptor> notFound = catalog.discover(ToolDiscoveryRequest.forCapability("network.http"));
        assertThat(notFound).isEmpty();
    }

    @Test
    void shouldGetToolDirectlyById() {
        catalog.register(dummyProvider);

        Optional<ToolDescriptor> desc = catalog.get(ToolId.of("test-fs"));
        assertThat(desc).isPresent();
        assertThat(desc.get().capabilityKeys()).contains("filesystem.read");

        Optional<ToolProvider> prov = catalog.providerFor(ToolId.of("test-fs"));
        assertThat(prov).isPresent();
    }

    @Test
    void shouldUnregisterProvider() {
        catalog.register(dummyProvider);
        assertThat(catalog.discover(ToolDiscoveryRequest.all())).hasSize(1);

        catalog.unregister(dummyProvider);
        assertThat(catalog.discover(ToolDiscoveryRequest.all())).isEmpty();
    }
}
