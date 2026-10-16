package tech.kayys.andalus.a2ui.andalus.surface;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaceOptions;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaces;
import tech.kayys.andalus.aqli.sdk.AgentRunEvent;
import tech.kayys.andalus.aqli.sdk.AgentRunEvents;
import tech.kayys.andalus.aqli.sdk.AgentRunEventsQuery;
import tech.kayys.andalus.aqli.sdk.AgentRunState;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RunEventsSurfaceTest {

    private final SurfaceJsonlTestSupport jsonl = new SurfaceJsonlTestSupport();

    @Test
    void rendersRunEventsSurfaceAndFacadeDelegates() throws Exception {
        AgentRunEvents events = runEventsPage();

        List<A2uiServerMessage> messages = RunEventsSurface.render(
                events,
                WayangA2uiSurfaceOptions.readOnly());

        assertThat(messages).isEqualTo(WayangA2uiSurfaces.runEvents(events, WayangA2uiSurfaceOptions.readOnly()));
        assertThat(messages).hasSize(3);

        JsonNode data = jsonl.dataModelUpdate(messages);
        JsonNode update = jsonl.surfaceUpdate(messages);
        JsonNode begin = jsonl.beginRendering(messages);

        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-events-run-1");
        assertThat(data.at("/dataModelUpdate/contents/0/valueString").asText()).isEqualTo("run-1");
        assertThat(data.at("/dataModelUpdate/contents/4/valueNumber").asLong()).isEqualTo(3L);
        assertThat(data.toString()).contains("event2").contains("run.completed");

        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(6);
        assertThat(update.toString())
                .contains("Refresh events")
                .contains("andalus.run.events")
                .contains("afterSequence")
                .contains("limit")
                .contains("run.completed");
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-events-run-1-root");
    }

    @Test
    void hidesRefreshActionWhenRunEventsActionIsNotVisible() {
        String jsonlStream = jsonl.stream(RunEventsSurface.render(
                runEventsPage(),
                WayangA2uiSurfaceOptions.inspectOnly()));

        assertThat(jsonlStream)
                .doesNotContain("Refresh events")
                .doesNotContain("\"name\":\"andalus.run.events\"");
    }

    private static AgentRunEvents runEventsPage() {
        return new AgentRunEvents(
                "run-1",
                AgentRunEventsQuery.of("", "", 1L, 10),
                List.of(
                        runningEvent(2),
                        completedEvent(3)),
                3,
                "Loaded run events.");
    }

    private static AgentRunEvent runningEvent(long sequence) {
        return new AgentRunEvent(
                "run-1",
                sequence,
                "run.running",
                AgentRunState.RUNNING,
                "Run is running.",
                Map.of());
    }

    private static AgentRunEvent completedEvent(long sequence) {
        return new AgentRunEvent(
                "run-1",
                sequence,
                "run.completed",
                AgentRunState.COMPLETED,
                "Run completed.",
                Map.of());
    }

}
