package tech.kayys.andalus.core;

import org.junit.jupiter.api.Test;

import tech.kayys.andalus.extension.Reference;
import tech.kayys.andalus.spi.agent.AgentIdentity;

import static org.assertj.core.api.Assertions.assertThat;

class AgentDefinitionTest {

    @Test
    void createsDeclarativeAgentDefinition() {

        var definition =
                AgentDefinition.builder()
                        .identity(
                                AgentIdentity.of(
                                        "research-agent"))
                        .role("researcher")
                        .goal(
                                "Research a given topic")
                        .instructions(
                                "Use reliable sources.")
                        .capability("research")
                        .capability("analysis")
                        .skill(new Reference("research"))
                        .tool(new Reference("web-search"))
                        .build();

        assertThat(definition.identity().id())
                .isEqualTo("research-agent");

        assertThat(definition.role())
                .isEqualTo("researcher");

        assertThat(definition.capabilities())
                .containsExactlyInAnyOrder(
                        "research",
                        "analysis");

        assertThat(definition.skills())
                .extracting(Reference::name)
                .containsExactly("research");

        assertThat(definition.tools())
                .extracting(Reference::name)
                .containsExactly("web-search");
    }

    @Test
    void validatesDefinition() {

        var definition =
                AgentDefinition.builder()
                        .identity(
                                AgentIdentity.of(
                                        "research-agent"))
                        .role("researcher")
                        .goal("Research topics")
                        .build();

        var result =
                AgentDefinitionValidator.validate(
                        definition);

        assertThat(result.valid())
                .isTrue();
    }
}
