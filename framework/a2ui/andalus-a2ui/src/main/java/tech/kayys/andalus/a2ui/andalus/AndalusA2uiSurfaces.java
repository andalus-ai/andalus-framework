package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.a2ui.andalus.surface.RunEventsSurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunHistorySurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunInspectionSurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunStatusSurface;
import tech.kayys.andalus.aqli.sdk.AgentRunEvents;
import tech.kayys.andalus.aqli.sdk.AgentRunHistory;
import tech.kayys.andalus.aqli.sdk.AgentRunInspection;
import tech.kayys.andalus.aqli.sdk.AgentRunStatus;

import java.util.List;

/**
 * Maps common Wayang SDK models to A2UI surface message streams.
 */
public final class WayangA2uiSurfaces {

    private WayangA2uiSurfaces() {
    }

    public static List<A2uiServerMessage> runStatus(AgentRunStatus status) {
        return runStatus(status, WayangA2uiSurfaceOptions.readOnly());
    }

    public static List<A2uiServerMessage> runStatus(AgentRunStatus status, WayangA2uiSurfaceOptions options) {
        return RunStatusSurface.render(status, options);
    }

    public static List<A2uiServerMessage> runInspection(AgentRunInspection inspection) {
        return runInspection(inspection, WayangA2uiSurfaceOptions.readOnly());
    }

    public static List<A2uiServerMessage> runInspection(
            AgentRunInspection inspection,
            WayangA2uiSurfaceOptions options) {
        return RunInspectionSurface.render(inspection, options);
    }

    public static List<A2uiServerMessage> runEvents(AgentRunEvents events) {
        return runEvents(events, WayangA2uiSurfaceOptions.readOnly());
    }

    public static List<A2uiServerMessage> runEvents(AgentRunEvents events, WayangA2uiSurfaceOptions options) {
        return RunEventsSurface.render(events, options);
    }

    public static List<A2uiServerMessage> runHistory(AgentRunHistory history) {
        return runHistory(history, WayangA2uiSurfaceOptions.readOnly());
    }

    public static List<A2uiServerMessage> runHistory(AgentRunHistory history, WayangA2uiSurfaceOptions options) {
        return RunHistorySurface.render(history, options);
    }

}
