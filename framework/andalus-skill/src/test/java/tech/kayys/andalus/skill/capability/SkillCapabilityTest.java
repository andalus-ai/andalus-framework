package tech.kayys.andalus.skill.capability;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.spi.SkillKind;
import tech.kayys.andalus.skill.spi.SkillManifest;
import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkillCapabilityTest {

    @Test
    void capabilityRequirementCreationAndValidation() {
        CapabilityRequirement req = CapabilityRequirement.required("web.search");
        assertThat(req.capabilityId()).isEqualTo("web.search");
        assertThat(req.required()).isTrue();

        CapabilityRequirement opt = CapabilityRequirement.optional("cache.redis");
        assertThat(opt.capabilityId()).isEqualTo("cache.redis");
        assertThat(opt.required()).isFalse();

        assertThatThrownBy(() -> new CapabilityRequirement(null, true))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new CapabilityRequirement("   ", true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void capabilityResolutionResultFactoryMethods() {
        CapabilityRequirement r1 = CapabilityRequirement.required("c1");
        CapabilityRequirement r2 = CapabilityRequirement.required("c2");

        CapabilityResolutionResult satisfied = CapabilityResolutionResult.satisfied(Set.of(r1, r2));
        assertThat(satisfied.satisfied()).isTrue();
        assertThat(satisfied.required()).containsExactlyInAnyOrder(r1, r2);
        assertThat(satisfied.missing()).isEmpty();

        CapabilityResolutionResult unsatisfied = CapabilityResolutionResult.unsatisfied(Set.of(r1, r2), Set.of(r2));
        assertThat(unsatisfied.satisfied()).isFalse();
        assertThat(unsatisfied.required()).containsExactlyInAnyOrder(r1, r2);
        assertThat(unsatisfied.missing()).containsExactly(r2);

        CapabilityResolutionResult empty = CapabilityResolutionResult.empty();
        assertThat(empty.satisfied()).isTrue();
        assertThat(empty.required()).isEmpty();
        assertThat(empty.missing()).isEmpty();
    }

    @Test
    void skillCapabilityRequirementsExtractedFromManifest() {
        SkillManifest manifest = new SkillManifest(
                "web-research",
                "1.0.0",
                "Web Research",
                "Researches topics",
                SkillKind.COMPOSITE,
                "research",
                Set.of("web.search", "web.open"),
                List.of(SkillReference.of("sub-skill")),
                List.of(),
                Set.of(),
                Map.of(),
                Map.of());

        Set<CapabilityRequirement> requirements = SkillCapabilityRequirements.from(manifest);
        assertThat(requirements).hasSize(2);
        assertThat(requirements).extracting(CapabilityRequirement::capabilityId)
                .containsExactlyInAnyOrder("web.search", "web.open");
        assertThat(requirements).allMatch(CapabilityRequirement::required);

        assertThat(SkillCapabilityRequirements.from(null)).isEmpty();
    }

    @Test
    void skillCapabilityResolverAlwaysSatisfied() throws Exception {
        SkillCapabilityResolver resolver = SkillCapabilityResolver.alwaysSatisfied();
        CapabilityRequirement req = CapabilityRequirement.required("db.sql");

        CapabilityResolutionResult result = resolver.resolve(Set.of(req)).get();
        assertThat(result.satisfied()).isTrue();
        assertThat(result.required()).containsExactly(req);
        assertThat(result.missing()).isEmpty();

        CapabilityResolutionResult emptyResult = resolver.resolve(Set.of()).get();
        assertThat(emptyResult.satisfied()).isTrue();
    }

    @Test
    void skillCapabilityResolverAlwaysUnsatisfied() throws Exception {
        SkillCapabilityResolver resolver = SkillCapabilityResolver.alwaysUnsatisfied();
        CapabilityRequirement req = CapabilityRequirement.required("hardware.gpu");

        CapabilityResolutionResult result = resolver.resolve(Set.of(req)).get();
        assertThat(result.satisfied()).isFalse();
        assertThat(result.required()).containsExactly(req);
        assertThat(result.missing()).containsExactly(req);
    }
}
