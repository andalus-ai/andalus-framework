package tech.kayys.andalus.spi.capability;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapabilityGraphTest {

    private Capability mockCapability(String id) {
        return new Capability() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public CapabilityType type() {
                return CapabilityType.of("tool", id);
            }

            @Override
            public CapabilityDescriptor descriptor() {
                return CapabilityDescriptor.of(id, "Test capability " + id, type());
            }
        };
    }

    @Test
    void buildsAndExpandsGraph() {
        Capability browser = mockCapability("browser");
        Capability http = mockCapability("http");
        Capability extract = mockCapability("web.extract");
        Capability search = mockCapability("search");
        Capability citation = mockCapability("citation");
        Capability research = mockCapability("research");

        CapabilityGraph graph = CapabilityGraphBuilder.builder()
                .capability(browser)
                .capability(http)
                .capability(extract)
                .capability(search)
                .capability(citation)
                .capability(research)
                .parent("browser", "research")
                .parent("search", "research")
                .requiredDependency("research", "search")
                .requiredDependency("research", "citation")
                .optionalDependency("research", "browser")
                .requiredDependency("search", "http")
                .requiredDependency("browser", "http")
                .requiredDependency("browser", "web.extract")
                .build();

        assertThat(graph.contains("research")).isTrue();
        assertThat(graph.ancestorsOf("browser")).containsExactly("research");

        Set<String> expanded = graph.expand("research");
        assertThat(expanded).contains("research", "search", "citation", "browser", "http", "web.extract");

        CapabilityRequirements requirements = CapabilityRequirements.of(
                CapabilityRequirement.required("research")
        );

        DefaultCapabilityGraphResolver resolver = new DefaultCapabilityGraphResolver();
        CapabilityGraphResolver.CapabilityGraphResolution resolution = resolver.resolve(requirements, graph);

        assertThat(resolution.satisfied()).isTrue();
        assertThat(resolution.requested()).containsExactly("research");
        assertThat(resolution.expanded()).contains("research", "search", "citation", "browser", "http", "web.extract");
        assertThat(resolution.missing()).isEmpty();
    }

    @Test
    void detectsCycleInDependencies() {
        Capability a = mockCapability("A");
        Capability b = mockCapability("B");

        CapabilityGraphBuilder builder = CapabilityGraphBuilder.builder()
                .capability(a)
                .capability(b)
                .requiredDependency("A", "B")
                .requiredDependency("B", "A");

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cycle");
    }

    @Test
    void detectsUnknownDependency() {
        Capability a = mockCapability("A");

        CapabilityGraphBuilder builder = CapabilityGraphBuilder.builder()
                .capability(a)
                .requiredDependency("A", "UNKNOWN");

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("depends on unknown capability");
    }

    @Test
    void reportsMissingRequiredDependencyDuringResolution() {
        Capability research = mockCapability("research");
        Capability search = mockCapability("search");

        CapabilityGraph graph = CapabilityGraphBuilder.builder()
                .capability(research)
                .capability(search)
                .requiredDependency("research", "search")
                .build();

        CapabilityRequirements requirements = CapabilityRequirements.of(
                CapabilityRequirement.required("research"),
                CapabilityRequirement.required("missing-cap")
        );

        DefaultCapabilityGraphResolver resolver = new DefaultCapabilityGraphResolver();
        CapabilityGraphResolver.CapabilityGraphResolution resolution = resolver.resolve(requirements, graph);

        assertThat(resolution.satisfied()).isFalse();
        assertThat(resolution.missing()).contains("missing-cap");
    }
}
