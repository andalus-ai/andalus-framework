package tech.kayys.wayang.execution.core.replay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.replay.EffectDecision;
import tech.kayys.wayang.execution.replay.EffectMode;
import tech.kayys.wayang.execution.replay.ExecutionFixture;
import tech.kayys.wayang.execution.replay.ReplayDecision;
import tech.kayys.wayang.execution.replay.ReplayManifest;
import tech.kayys.wayang.execution.replay.ReplayMode;
import tech.kayys.wayang.execution.replay.ReplayResult;
import tech.kayys.wayang.execution.replay.ReplaySafety;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionReplayTest {

    @Test
    @DisplayName("FixedClockProvider maintains deterministic time and advances reliably")
    void testFixedClockProvider() {
        Instant t0 = Instant.parse("2026-09-20T10:00:00Z");
        FixedClockProvider clock = new FixedClockProvider(t0);

        assertEquals(t0, clock.now());
        assertEquals(t0, clock.monotonicTime());

        clock.advanceSeconds(30);
        assertEquals(t0.plusSeconds(30), clock.now());
    }

    @Test
    @DisplayName("DeterministicRandomProvider reproduces identical stream given same seed")
    void testDeterministicRandomProvider() {
        DeterministicRandomProvider r1 = new DeterministicRandomProvider(12345L);
        DeterministicRandomProvider r2 = new DeterministicRandomProvider(12345L);

        assertEquals(r1.nextLong(), r2.nextLong());
        assertEquals(r1.nextDouble(), r2.nextDouble());
        assertEquals(r1.nextLong(), r2.nextLong());
    }

    @Test
    @DisplayName("DefaultEffectSandbox blocks destructive actions and virtualizes writes")
    void testEffectSandbox() {
        DefaultEffectSandbox sandbox = new DefaultEffectSandbox();

        // Destructive effects blocked
        EffectDecision dEmail = sandbox.evaluate("send_email", Map.of());
        assertFalse(dEmail.allowed());
        assertEquals(EffectMode.BLOCKED, dEmail.mode());

        EffectDecision dDeploy = sandbox.evaluate("deployment", Map.of());
        assertFalse(dDeploy.allowed());
        assertEquals(EffectMode.BLOCKED, dDeploy.mode());

        // File writes virtualized
        EffectDecision dFile = sandbox.evaluate("write_file", Map.of());
        assertTrue(dFile.allowed());
        assertEquals(EffectMode.VIRTUAL, dFile.mode());
    }

    @Test
    @DisplayName("DefaultReplayAuthority and DefaultReplayEngine execute simulation safely")
    void testReplayEngine() {
        DefaultReplayAuthority authority = new DefaultReplayAuthority();
        DefaultReplayEngine engine = new DefaultReplayEngine(authority);

        ReplayManifest simManifest = ReplayManifest.simulation("exec-replay-1", 42L);
        DefaultReplayEnvironment env = DefaultReplayEnvironment.standard("env-sim-1");

        ReplayResult result = engine.replay(simManifest, env);
        assertTrue(result.success());
        assertEquals("exec-replay-1", result.executionId());
        assertEquals(ReplayMode.SIMULATE, result.modeUsed());
        assertEquals(42L, result.summary().get("stateSequence"));

        // Live execution requires approval
        ReplayManifest liveManifest = new ReplayManifest("exec-live-1", 1L, "v1", ReplayMode.REEXECUTE, ReplaySafety.LIVE_HAZARDOUS);
        ReplayDecision dec = authority.authorize("exec-live-1", liveManifest);
        assertEquals(ReplayDecision.REQUIRE_APPROVAL, dec);
    }

    @Test
    @DisplayName("DefaultExecutionTestHarness runs test fixtures deterministically")
    void testExecutionTestHarness() {
        DefaultExecutionTestHarness harness = new DefaultExecutionTestHarness();
        ReplayManifest manifest = ReplayManifest.simulation("exec-fixture-1", 10L);
        ExecutionFixture fixture = new ExecutionFixture("golden-run", manifest, Map.of("param1", "override"));

        ReplayResult testResult = harness.runTest(fixture);
        assertTrue(testResult.success());
        assertEquals("exec-fixture-1", testResult.executionId());
    }
}
