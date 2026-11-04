package tech.kayys.andalus.harness.planning;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanStepIdTest {

    @Test
    void rejectsBlankValues() {
        assertThatThrownBy(() -> PlanStepId.of("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void rejectsNullValues() {
        assertThatThrownBy(() -> PlanStepId.of(null))
                .isInstanceOf(NullPointerException.class);
    }
}
