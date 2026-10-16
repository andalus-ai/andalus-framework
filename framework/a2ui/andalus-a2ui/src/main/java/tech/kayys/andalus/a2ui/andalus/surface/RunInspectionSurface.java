package tech.kayys.andalus.a2ui.andalus.surface;

import tech.kayys.andalus.a2ui.core.A2uiServerMessage;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiActions;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiSurfaceOptions;
import tech.kayys.andalus.aqli.sdk.AgentRunInspection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Composes Wayang run inspection data into status and event A2UI surfaces.
 */
public final class RunInspectionSurface {

    private RunInspectionSurface() {
    }

    public static List<A2uiServerMessage> render(AgentRunInspection inspection, WayangA2uiSurfaceOptions options) {
        AgentRunInspection resolved = Objects.requireNonNull(inspection, "inspection");
        WayangA2uiSurfaceOptions resolvedOptions = SurfaceActions.options(options);
        List<A2uiServerMessage> messages = new ArrayList<>(
                RunStatusSurface.render(resolved.status(), resolvedOptions));
        if (resolvedOptions.shows(WayangA2uiActions.RUN_EVENTS) && !resolved.events().empty()) {
            messages.addAll(RunEventsSurface.render(resolved.events(), resolvedOptions));
        }
        return List.copyOf(messages);
    }
}
