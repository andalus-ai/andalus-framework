package tech.kayys.andalus.a2ui.andalus.surface;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaceOptions;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaces;
import tech.kayys.andalus.aqli.sdk.AgentRunHandle;
import tech.kayys.andalus.aqli.sdk.AgentRunState;
import tech.kayys.andalus.aqli.sdk.AgentRunStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RunStatusSurfaceTest {

    private final SurfaceJsonlTestSupport jsonl = new SurfaceJsonlTestSupport();

    @Test
    void rendersRunStatusSurfaceAndFacadeDelegates() throws Exception {
        AgentRunStatus status = new AgentRunStatus(
                AgentRunHandle.completed("RUN 123", "react"),
                true,
                "Run completed.",
                Map.of());

        List<A2uiServerMessage> messages = RunStatusSurface.render(
                status,
                WayangA2uiSurfaceOptions.readOnly());

        assertThat(messages).isEqualTo(WayangA2uiSurfaces.runStatus(status, WayangA2uiSurfaceOptions.readOnly()));
        assertThat(messages).hasSize(3);

        JsonNode data = jsonl.dataModelUpdate(messages);
        JsonNode update = jsonl.surfaceUpdate(messages);
        JsonNode begin = jsonl.beginRendering(messages);

        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-run-123");
        assertThat(data.at("/dataModelUpdate/contents/0/valueString").asText()).isEqualTo("RUN 123");
        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(5);
        assertThat(update.toString())
                .contains("andalus.run.inspect")
                .contains("andalus.run.events");
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-run-123-root");
    }

    @Test
    void hidesLifecycleActionsForTerminalRuns() {
        AgentRunStatus running = runningStatus();
        AgentRunStatus completed = completedStatus();

        String runningJsonl = jsonl.stream(RunStatusSurface.render(
                running,
                WayangA2uiSurfaceOptions.runLifecycle()));
        String completedJsonl = jsonl.stream(RunStatusSurface.render(
                completed,
                WayangA2uiSurfaceOptions.runLifecycle()));

        assertThat(runningJsonl)
                .contains("andalus.run.wait")
                .contains("andalus.run.cancel");
        assertThat(completedJsonl)
                .doesNotContain("andalus.run.wait")
                .doesNotContain("andalus.run.cancel");
    }

    private static AgentRunStatus runningStatus() {
        return new AgentRunStatus(
                new AgentRunHandle("run-1", AgentRunState.RUNNING, "react"),
                true,
                "Run is running.",
                Map.of());
    }

    private static AgentRunStatus completedStatus() {
        return new AgentRunStatus(
                AgentRunHandle.completed("run-2", "react"),
                true,
                "Run completed.",
                Map.of());
    }

}
