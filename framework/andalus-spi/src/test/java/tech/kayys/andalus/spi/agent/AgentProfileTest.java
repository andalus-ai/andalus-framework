package tech.kayys.andalus.spi.agent;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AgentProfileTest {

    @Test
    void createsDefaultProfile() {

        var profile =
                AgentProfile.defaults(
                        "desktop");

        assertThat(profile.id())
                .isEqualTo("desktop");

        assertThat(
                profile.executionLimits()
                        .maxDuration())
                .isEqualTo(Duration.ofMinutes(5));

        assertThat(
                profile.securityPolicy()
                        .failClosed())
                .isTrue();
    }

    @Test
    void profileControlsCapabilities() {

        var profile =
                new AgentProfile(
                        "research",
                        "Research Profile",
                        Set.of(
                                "research",
                                "browser"),
                        java.util.Map.of(),
                        AgentProfile.ExecutionLimits.defaults(),
                        AgentProfile.ModelPolicy.defaults(),
                        AgentProfile.ResourcePolicy.defaults(),
                        AgentProfile.SecurityPolicy.defaults(),
                        java.util.Map.of());

        assertThat(profile.enables("research"))
                .isTrue();

        assertThat(profile.enables("shell"))
                .isFalse();
    }
}
