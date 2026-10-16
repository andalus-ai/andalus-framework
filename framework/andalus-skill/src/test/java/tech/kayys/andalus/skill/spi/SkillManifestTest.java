package tech.kayys.andalus.skill.spi;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkillManifestTest {

    @Test
    void parsesInstructionSkill() {
        Map<String, Object> manifest = Map.of(
                "id", "web-research",
                "version", "1.0.0",
                "name", "Web Research",
                "kind", "INSTRUCTION",
                "capabilities", List.of("web.search")
        );

        SkillManifest result = new DefaultSkillManifestParser().parse(manifest);

        assertThat(result.id()).isEqualTo("web-research");
        assertThat(result.kind()).isEqualTo(SkillKind.INSTRUCTION);
        assertThat(result.requiresCapability("web.search")).isTrue();
    }

    @Test
    void parsesCompositeSkill() {
        Map<String, Object> manifest = Map.of(
                "id", "research",
                "version", "1.0.0",
                "name", "Research",
                "kind", "COMPOSITE",
                "skills", List.of("source-validation"),
                "tools", List.of("web.search")
        );

        SkillManifest result = new DefaultSkillManifestParser().parse(manifest);

        assertThat(result.usesSkill("source-validation")).isTrue();
        assertThat(result.usesTool("web.search")).isTrue();
    }

    @Test
    void rejectsCompositeWithoutDependencies() {
        SkillManifest manifest = new SkillManifest(
                "research",
                "1.0.0",
                "Research",
                "",
                SkillKind.COMPOSITE,
                "",
                Set.of(),
                List.of(),
                List.of(),
                Set.of(),
                Map.of(),
                Map.of()
        );

        List<String> errors = new DefaultSkillManifestValidator().validate(manifest);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).contains("COMPOSITE");
    }

    @Test
    void manifestRoundTrips() {
        SkillManifest original = new SkillManifest(
                "web-research",
                "1.0.0",
                "Web Research",
                "Research web sources",
                SkillKind.COMPOSITE,
                "research",
                Set.of("web.search"),
                List.of(SkillReference.of("source-validation")),
                List.of(SkillReference.of("web.search")),
                Set.of(),
                Map.of("maxSources", 10),
                Map.of("author", "kayys")
        );

        DefaultSkillManifestParser parser = new DefaultSkillManifestParser();
        SkillManifest restored = parser.parse(parser.serialize(original));

        assertThat(restored).isEqualTo(original);
    }

    @Test
    void validatorThrowsOnInvalidManifest() {
        SkillManifest invalidManifest = new SkillManifest(
                "invalid-comp",
                "1.0.0",
                "Invalid Comp",
                "",
                SkillKind.COMPOSITE,
                "",
                Set.of(),
                List.of(),
                List.of(),
                Set.of(),
                Map.of(),
                Map.of()
        );

        DefaultSkillManifestValidator validator = new DefaultSkillManifestValidator();
        assertThatThrownBy(() -> validator.validateOrThrow(invalidManifest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("COMPOSITE");
    }
}
