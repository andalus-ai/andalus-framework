package tech.kayys.wayang.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.catalog.ToolCatalog;
import tech.kayys.wayang.tool.resolution.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolResolverTest {

    private ToolCatalog catalog;
    private ToolDescriptor tool1;
    private ToolDescriptor tool2;

    @BeforeEach
    void setUp() {
        catalog = ToolCatalog.create();
        tool1 = DefaultToolDescriptor.builder(ToolId.of("local-fs"), "LocalFs")
                .capabilities(Set.of("filesystem.read"))
                .executionProfile(ToolExecutionProfile.readOnly(ToolKind.READ))
                .build();
        tool2 = DefaultToolDescriptor.builder(ToolId.of("sandbox-fs"), "SandboxFs")
                .capabilities(Set.of("filesystem.read"))
                .executionProfile(ToolExecutionProfile.readOnly(ToolKind.READ))
                .build();

        ToolProvider provider = new ToolProvider() {
            @Override
            public Optional<ToolDescriptor> describe(ToolId id) {
                if (id.equals(tool1.idAsToolId())) return Optional.of(tool1);
                if (id.equals(tool2.idAsToolId())) return Optional.of(tool2);
                return Optional.empty();
            }

            @Override
            public Collection<ToolDescriptor> tools() {
                return List.of(tool1, tool2);
            }

            @Override
            public ToolExecutor executor(ToolId id) {
                return ToolExecutor.synchronous((inv, ctx) -> ToolResult.success(inv.invocationIdentifier(), "read", Duration.ZERO, "fs"));
            }
        };
        catalog.register(provider);
    }

    @Test
    void shouldResolveFirstCandidateByDefault() {
        ToolResolver resolver = new DefaultToolResolver(catalog);
        ToolIntent intent = ToolIntent.of("filesystem.read", ToolArguments.empty(), null);

        Optional<ToolResolution> res = resolver.resolve(intent, ToolResolutionContext.empty());
        assertThat(res).isPresent();
        assertThat(res.get().tool().idAsToolId()).isEqualTo(ToolId.of("local-fs"));
    }

    @Test
    void shouldResolveWithCustomSelectionPolicy() {
        ToolSelectionPolicy sandboxPreferred = (intent, candidates, context) ->
                candidates.stream().filter(c -> c.idAsToolId().value().contains("sandbox")).findFirst();

        ToolResolver resolver = new DefaultToolResolver(catalog, sandboxPreferred);
        ToolIntent intent = ToolIntent.of("filesystem.read", ToolArguments.empty(), null);

        Optional<ToolResolution> res = resolver.resolve(intent, ToolResolutionContext.empty());
        assertThat(res).isPresent();
        assertThat(res.get().tool().idAsToolId()).isEqualTo(ToolId.of("sandbox-fs"));
    }
}
