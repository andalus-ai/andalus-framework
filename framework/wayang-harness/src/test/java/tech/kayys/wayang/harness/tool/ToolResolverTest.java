package tech.kayys.wayang.harness.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

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
                if (id.equals(tool1.id())) return Optional.of(tool1);
                if (id.equals(tool2.id())) return Optional.of(tool2);
                return Optional.empty();
            }

            @Override
            public Collection<ToolDescriptor> tools() {
                return List.of(tool1, tool2);
            }

            @Override
            public ToolExecutor executor(ToolId id) {
                return (inv, ctx) -> ToolResult.success(inv.id(), "read", java.time.Duration.ZERO, "fs");
            }
        };
        catalog.register(provider);
    }

    @Test
    void shouldResolveFirstCandidateByDefault() {
        ToolResolver resolver = new DefaultToolResolver(catalog);
        ToolIntent intent = ToolIntent.of("filesystem.read", ToolArguments.empty(), null);

        Optional<ToolResolution> res = resolver.resolve(intent, ToolResolutionContext.of(null, null));
        assertThat(res).isPresent();
        assertThat(res.get().tool().id()).isEqualTo(ToolId.of("local-fs"));
    }

    @Test
    void shouldResolveWithCustomSelectionPolicy() {
        ToolSelectionPolicy sandboxPreferred = (intent, candidates, context) ->
                candidates.stream().filter(c -> c.id().value().contains("sandbox")).findFirst();

        ToolResolver resolver = new DefaultToolResolver(catalog, sandboxPreferred);
        ToolIntent intent = ToolIntent.of("filesystem.read", ToolArguments.empty(), null);

        Optional<ToolResolution> res = resolver.resolve(intent, ToolResolutionContext.of(null, null));
        assertThat(res).isPresent();
        assertThat(res.get().tool().id()).isEqualTo(ToolId.of("sandbox-fs"));
    }
}
