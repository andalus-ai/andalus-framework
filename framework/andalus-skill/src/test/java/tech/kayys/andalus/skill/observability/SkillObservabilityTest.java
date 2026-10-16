package tech.kayys.andalus.skill.observability;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class SkillObservabilityTest {

    @Test
    void observedSkillRuntimeRecordsSuccessEvents() throws Exception {
        InMemorySkillObservationRecorder recorder = new InMemorySkillObservationRecorder();

        SkillRuntime mockRuntime = req -> CompletableFuture.completedFuture(
                SkillResult.success(Map.of("out", "ok"))
        );

        ObservedSkillRuntime observedRuntime = new ObservedSkillRuntime(mockRuntime, recorder);

        SkillReference skillRef = SkillReference.of("skill-echo");
        SkillExecutionRequest request = SkillExecutionRequest.of(
                skillRef, Map.of("in", "val"), "exec-1", "trace-1", "turn-1"
        );

        SkillResult result = observedRuntime.execute(request).toCompletableFuture().get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(recorder.startedEvents()).hasSize(1);
        assertThat(recorder.completedRecords()).hasSize(1);

        SkillObservationContext startCtx = recorder.startedEvents().get(0);
        assertThat(startCtx.skillId()).isEqualTo("skill-echo");
        assertThat(startCtx.executionId()).isEqualTo("exec-1");
        assertThat(startCtx.traceId()).isEqualTo("trace-1");
        assertThat(startCtx.turnId()).isEqualTo("turn-1");

        InMemorySkillObservationRecorder.ExecutionRecord record = recorder.completedRecords().get(0);
        assertThat(record.isSuccess()).isTrue();
        assertThat(record.error()).isNull();
        assertThat(record.duration()).isNotNull();
    }

    @Test
    void observedSkillRuntimeRecordsFailureEvents() throws Exception {
        InMemorySkillObservationRecorder recorder = new InMemorySkillObservationRecorder();

        SkillRuntime mockRuntime = req -> {
            CompletableFuture<SkillResult> f = new CompletableFuture<>();
            f.completeExceptionally(new RuntimeException("Simulated failure"));
            return f;
        };

        ObservedSkillRuntime observedRuntime = new ObservedSkillRuntime(mockRuntime, recorder);

        SkillExecutionRequest request = SkillExecutionRequest.of(
                SkillReference.of("failing-skill"), Map.of()
        );

        try {
            observedRuntime.execute(request).toCompletableFuture().get();
        } catch (Exception ignored) {
        }

        assertThat(recorder.startedEvents()).hasSize(1);
        assertThat(recorder.completedRecords()).hasSize(1);

        InMemorySkillObservationRecorder.ExecutionRecord record = recorder.completedRecords().get(0);
        assertThat(record.isSuccess()).isFalse();
        assertThat(record.error()).isNotNull();
        assertThat(record.error().getMessage()).contains("Simulated failure");
    }

    @Test
    void noopRecorderNeverThrows() {
        NoopSkillObservationRecorder recorder = NoopSkillObservationRecorder.INSTANCE;
        SkillObservationContext ctx = new SkillObservationContext("inv-1", "e-1", "t-1", "u-1", "s-1", "1.0", null);

        recorder.executionStarted(ctx);
        recorder.executionCompleted(ctx, SkillResult.success(Map.of()));
        recorder.executionFailed(ctx, new RuntimeException("error"));
    }
}
