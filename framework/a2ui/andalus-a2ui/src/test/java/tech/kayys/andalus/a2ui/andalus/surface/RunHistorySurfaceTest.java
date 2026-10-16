package tech.kayys.andalus.a2ui.andalus.surface;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaceOptions;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaces;
import tech.kayys.andalus.aqli.sdk.AgentRunHandle;
import tech.kayys.andalus.aqli.sdk.AgentRunHistory;
import tech.kayys.andalus.aqli.sdk.AgentRunHistoryQuery;
import tech.kayys.andalus.aqli.sdk.AgentRunState;
import tech.kayys.andalus.aqli.sdk.AgentRunStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RunHistorySurfaceTest {

    private final SurfaceJsonlTestSupport jsonl = new SurfaceJsonlTestSupport();

    @Test
    void rendersRunHistorySurfaceAndFacadeDelegates() throws Exception {
        AgentRunHistory history = runHistory(4);

        List<A2uiServerMessage> messages = RunHistorySurface.render(
                history,
                WayangA2uiSurfaceOptions.readOnly());

        assertThat(messages).isEqualTo(WayangA2uiSurfaces.runHistory(history, WayangA2uiSurfaceOptions.readOnly()));
        assertThat(messages).hasSize(3);

        JsonNode data = jsonl.dataModelUpdate(messages);
        JsonNode update = jsonl.surfaceUpdate(messages);
        JsonNode begin = jsonl.beginRendering(messages);

        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-history");
        assertThat(data.at("/dataModelUpdate/contents/0/valueNumber").asInt()).isEqualTo(4);
        assertThat(data.at("/dataModelUpdate/contents/1/valueNumber").asInt()).isEqualTo(2);
        assertThat(data.at("/dataModelUpdate/contents/3/valueBoolean").asBoolean()).isTrue();
        assertThat(data.toString()).contains("run0").contains("RUNNING").contains("run1").contains("COMPLETED");

        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(5);
        assertThat(update.at("/surfaceUpdate/components").size()).isEqualTo(16);
        assertThat(update.toString())
                .contains("Wayang runs")
                .contains("run-1")
                .contains("run-2")
                .contains("andalus.run.inspect")
                .contains("andalus.run.events");
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-history-root");
    }

    @Test
    void hidesLifecycleActionsForTerminalHistoryRows() {
        String jsonlStream = jsonl.stream(RunHistorySurface.render(
                runHistory(2),
                WayangA2uiSurfaceOptions.runLifecycle()));

        assertThat(jsonlStream)
                .contains("andalus.run.wait")
                .contains("andalus.run.cancel")
                .doesNotContain("andalus-run-history-run-run-2-wait")
                .doesNotContain("andalus-run-history-run-run-2-cancel");
    }

    private static AgentRunHistory runHistory(int totalRuns) {
        return new AgentRunHistory(
                AgentRunHistoryQuery.of("", 2, "", "", "", 0),
                List.of(runningStatusWithSurface(), completedStatusWithSurface()),
                totalRuns,
                "Loaded run history.");
    }

    private static AgentRunStatus runningStatusWithSurface() {
        return new AgentRunStatus(
                new AgentRunHandle("run-1", AgentRunState.RUNNING, "react"),
                true,
                "Run is running.",
                Map.of("surface", "coding-agent"));
    }

    private static AgentRunStatus completedStatusWithSurface() {
        return new AgentRunStatus(
                AgentRunHandle.completed("run-2", "react"),
                true,
                "Run completed.",
                Map.of("surface", "coding-agent"));
    }
}
