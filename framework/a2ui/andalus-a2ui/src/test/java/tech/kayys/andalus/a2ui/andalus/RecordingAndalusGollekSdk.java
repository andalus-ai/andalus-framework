package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.aqli.sdk.AgentRunCancelResult;
import tech.kayys.andalus.aqli.sdk.AgentRunEvent;
import tech.kayys.andalus.aqli.sdk.AgentRunEvents;
import tech.kayys.andalus.aqli.sdk.AgentRunEventsQuery;
import tech.kayys.andalus.aqli.sdk.AgentRunHandle;
import tech.kayys.andalus.aqli.sdk.AgentRunHistory;
import tech.kayys.andalus.aqli.sdk.AgentRunHistoryQuery;
import tech.kayys.andalus.aqli.sdk.AgentRunInspection;
import tech.kayys.andalus.aqli.sdk.AgentRunRequest;
import tech.kayys.andalus.aqli.sdk.AgentRunResult;
import tech.kayys.andalus.aqli.sdk.AgentRunState;
import tech.kayys.andalus.aqli.sdk.AgentRunStatus;
import tech.kayys.andalus.aqli.sdk.AgentRunWaitOptions;
import tech.kayys.andalus.aqli.sdk.AgentRunWaitResult;
import tech.kayys.andalus.aqli.sdk.HarnessPlan;
import tech.kayys.andalus.aqli.sdk.HarnessPlanRequest;
import tech.kayys.andalus.aqli.sdk.ProductSurface;
import tech.kayys.andalus.aqli.sdk.WayangAqliSdk;
import tech.kayys.andalus.aqli.sdk.WayangPlatformStatus;
import tech.kayys.andalus.aqli.sdk.WayangWorkbenchModel;
import tech.kayys.andalus.aqli.sdk.WorkspaceInspectionRequest;
import tech.kayys.andalus.aqli.sdk.WorkspaceSnapshot;

import java.util.List;
import java.util.Map;

final class RecordingWayangAqliSdk implements WayangAqliSdk {

    int inspected;
    int cancelled;
    int waited;
    int historyQueried;
    int eventsQueried;
    AgentRunHistoryQuery lastHistoryQuery = AgentRunHistoryQuery.all();
    AgentRunEventsQuery lastEventsQuery = AgentRunEventsQuery.all();
    String lastCancelReason = "";

    @Override
    public WayangPlatformStatus status() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProductSurface> productSurfaces() {
        return List.of();
    }

    @Override
    public WayangWorkbenchModel workbench() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkspaceSnapshot inspectWorkspace(WorkspaceInspectionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HarnessPlan planHarness(HarnessPlanRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AgentRunResult run(AgentRunRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AgentRunInspection inspectRun(String runId, AgentRunEventsQuery query) {
        inspected++;
        return new AgentRunInspection(
                runId,
                status(runId, AgentRunState.RUNNING, "Run is running."),
                null,
                "Inspected run.");
    }

    @Override
    public AgentRunHistory runHistory(AgentRunHistoryQuery query) {
        historyQueried++;
        lastHistoryQuery = query == null ? AgentRunHistoryQuery.all() : query;
        return new AgentRunHistory(
                lastHistoryQuery,
                List.of(
                        status("run-1", AgentRunState.RUNNING, "Run is running."),
                        status("run-2", AgentRunState.COMPLETED, "Run completed.")),
                2,
                "Loaded run history.");
    }

    @Override
    public AgentRunEvents runEvents(String runId, AgentRunEventsQuery query) {
        eventsQueried++;
        lastEventsQuery = query == null ? AgentRunEventsQuery.all() : query;
        return new AgentRunEvents(
                runId,
                lastEventsQuery,
                List.of(
                        new AgentRunEvent(runId, 1, "run.running", AgentRunState.RUNNING, "Run is running.", Map.of()),
                        new AgentRunEvent(runId, 2, "run.completed", AgentRunState.COMPLETED, "Run completed.", Map.of())),
                2,
                "Loaded run events.");
    }

    @Override
    public AgentRunCancelResult cancelRun(String runId, String reason) {
        cancelled++;
        lastCancelReason = reason;
        return AgentRunCancelResult.cancelled(status(runId, AgentRunState.CANCELLED, "Run was cancelled."));
    }

    @Override
    public AgentRunWaitResult waitForRun(String runId, AgentRunWaitOptions options) {
        waited++;
        AgentRunStatus status = status(runId, AgentRunState.COMPLETED, "Run completed.");
        return new AgentRunWaitResult(runId, status, true, false, 1, 0, "Run completed.", Map.of());
    }

    private static AgentRunStatus status(String runId, AgentRunState state, String message) {
        return new AgentRunStatus(new AgentRunHandle(runId, state, "react"), true, message, Map.of());
    }
}
