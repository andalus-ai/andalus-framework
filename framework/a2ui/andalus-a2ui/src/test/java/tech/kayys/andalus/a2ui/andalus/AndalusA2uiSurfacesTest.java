package tech.kayys.andalus.a2ui.andalus;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.aqli.sdk.AgentRunEvents;
import tech.kayys.andalus.aqli.sdk.AgentRunHandle;
import tech.kayys.andalus.aqli.sdk.AgentRunHistory;
import tech.kayys.andalus.aqli.sdk.AgentRunInspection;
import tech.kayys.andalus.aqli.sdk.AgentRunState;
import tech.kayys.andalus.aqli.sdk.AgentRunStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WayangA2uiSurfacesTest {

    private final A2uiJsonlTestSupport jsonl = new A2uiJsonlTestSupport();

    @Test
    void mapsRunStatusToRenderableA2uiSurface() throws Exception {
        AgentRunStatus status = new AgentRunStatus(
                AgentRunHandle.completed("RUN 123", "react"),
                true,
                "Run completed.",
                Map.of("durationMillis", 12));

        List<A2uiServerMessage> messages = WayangA2uiSurfaces.runStatus(status);

        assertThat(messages).hasSize(3);
        JsonNode data = jsonl.dataModelUpdate(messages);
        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-run-123");
        assertThat(data.at("/dataModelUpdate/contents/0/valueString").asText()).isEqualTo("RUN 123");
        assertThat(data.at("/dataModelUpdate/contents/1/valueString").asText()).isEqualTo("COMPLETED");

        JsonNode update = jsonl.surfaceUpdate(messages);
        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(5);
        assertThat(update.toString())
                .contains("andalus.run.inspect")
                .contains("andalus.run.events");

        JsonNode begin = jsonl.beginRendering(messages);
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-run-123-root");
    }

    @Test
    void mapsRunInspectionToStatusAndEventsWhenEventsExist() {
        AgentRunInspection inspection = RunSurfaceFixtures.runInspection();

        List<A2uiServerMessage> messages = WayangA2uiSurfaces.runInspection(inspection);
        String jsonlStream = jsonl.stream(messages);

        assertThat(messages).hasSize(6);
        assertThat(jsonlStream)
                .contains("andalus-run-run-1")
                .contains("andalus-run-events-run-1")
                .contains("andalus.run.events");
    }

    @Test
    void appliesSurfaceOptionsToRunStatusActions() throws Exception {
        AgentRunStatus status = RunSurfaceFixtures.runningStatus();

        String inspectOnly = jsonl.stream(WayangA2uiSurfaces.runStatus(status, WayangA2uiSurfaceOptions.inspectOnly()));
        String lifecycle = jsonl.stream(WayangA2uiSurfaces.runStatus(status, WayangA2uiSurfaceOptions.runLifecycle()));
        String completedLifecycle = jsonl.stream(WayangA2uiSurfaces.runStatus(
                RunSurfaceFixtures.completedStatus(),
                WayangA2uiSurfaceOptions.runLifecycle()));

        JsonNode inspectUpdate = jsonl.surfaceUpdate(inspectOnly);
        assertThat(inspectUpdate.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(4);
        assertThat(inspectOnly)
                .contains("andalus.run.inspect")
                .doesNotContain("andalus.run.events")
                .doesNotContain("andalus.run.wait")
                .doesNotContain("andalus.run.cancel");

        JsonNode lifecycleUpdate = jsonl.surfaceUpdate(lifecycle);
        assertThat(lifecycleUpdate.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(7);
        assertThat(lifecycle)
                .contains("andalus.run.inspect")
                .contains("andalus.run.events")
                .contains("andalus.run.wait")
                .contains("andalus.run.cancel");
        assertThat(completedLifecycle)
                .contains("andalus.run.events")
                .doesNotContain("andalus.run.wait")
                .doesNotContain("andalus.run.cancel");
    }

    @Test
    void mapsRunHistoryToRenderableA2uiSurface() throws Exception {
        AgentRunHistory history = RunSurfaceFixtures.runHistory(4);

        List<A2uiServerMessage> messages = WayangA2uiSurfaces.runHistory(history);

        assertThat(messages).hasSize(3);
        JsonNode data = jsonl.dataModelUpdate(messages);
        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-history");
        assertThat(data.at("/dataModelUpdate/contents/0/valueNumber").asInt()).isEqualTo(4);
        assertThat(data.at("/dataModelUpdate/contents/1/valueNumber").asInt()).isEqualTo(2);
        assertThat(data.at("/dataModelUpdate/contents/3/valueBoolean").asBoolean()).isTrue();
        assertThat(data.toString()).contains("run0").contains("RUNNING").contains("run1").contains("COMPLETED");

        JsonNode update = jsonl.surfaceUpdate(messages);
        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(5);
        assertThat(update.at("/surfaceUpdate/components").size()).isEqualTo(16);
        assertThat(update.toString())
                .contains("run-1")
                .contains("run-2")
                .contains("andalus.run.inspect")
                .contains("andalus.run.events");

        JsonNode begin = jsonl.beginRendering(messages);
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-history-root");
    }

    @Test
    void appliesSurfaceOptionsToRunHistoryRowActions() {
        AgentRunHistory history = RunSurfaceFixtures.runHistory(2);

        String jsonlStream = jsonl.stream(WayangA2uiSurfaces.runHistory(
                history,
                WayangA2uiSurfaceOptions.runLifecycle()));

        assertThat(jsonlStream)
                .contains("andalus.run.inspect")
                .contains("andalus.run.events")
                .contains("andalus.run.wait")
                .contains("andalus.run.cancel")
                .doesNotContain("andalus-run-history-run-run-2-wait")
                .doesNotContain("andalus-run-history-run-run-2-cancel");
    }

    @Test
    void mapsRunEventsToRenderableA2uiSurface() throws Exception {
        AgentRunEvents events = RunSurfaceFixtures.runEventsPage();

        List<A2uiServerMessage> messages = WayangA2uiSurfaces.runEvents(events);

        assertThat(messages).hasSize(3);
        JsonNode data = jsonl.dataModelUpdate(messages);
        assertThat(data.at("/dataModelUpdate/surfaceId").asText()).isEqualTo("andalus-run-events-run-1");
        assertThat(data.at("/dataModelUpdate/contents/0/valueString").asText()).isEqualTo("run-1");
        assertThat(data.at("/dataModelUpdate/contents/1/valueNumber").asInt()).isEqualTo(3);
        assertThat(data.at("/dataModelUpdate/contents/4/valueNumber").asLong()).isEqualTo(3L);
        assertThat(data.toString()).contains("event2").contains("run.completed");

        JsonNode update = jsonl.surfaceUpdate(messages);
        assertThat(update.at("/surfaceUpdate/components/0/component/Column/children/explicitList").size())
                .isEqualTo(6);
        assertThat(update.toString())
                .contains("Refresh events")
                .contains("andalus.run.events")
                .contains("afterSequence")
                .contains("run.completed");

        JsonNode begin = jsonl.beginRendering(messages);
        assertThat(begin.at("/beginRendering/root").asText()).isEqualTo("andalus-run-events-run-1-root");
    }
}
