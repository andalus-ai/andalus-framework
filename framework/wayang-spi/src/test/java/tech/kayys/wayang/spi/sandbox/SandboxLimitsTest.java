package tech.kayys.wayang.spi.sandbox;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SandboxLimitsTest {

    @Test
    void testLimitsValidation() {
        SandboxLimits limits = new SandboxLimits(
                1000L,
                512_000_000L,
                1_000_000_000L,
                50L,
                Duration.ofMinutes(5)
        );

        assertEquals(1000L, limits.cpuMillis());
        assertEquals(512_000_000L, limits.memoryBytes());
        assertEquals(Duration.ofMinutes(5), limits.executionTimeout());

        // -1L means unlimited in Phase 4.6, values < -1 are invalid
        assertThrows(IllegalArgumentException.class, () ->
                new SandboxLimits(-2L, 100L, 100L, 10L, Duration.ofSeconds(10))
        );

        assertThrows(IllegalArgumentException.class, () ->
                new SandboxLimits(100L, 100L, 100L, 10L, Duration.ofSeconds(-1))
        );
    }
}
