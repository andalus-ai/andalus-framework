package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.http.HttpIssueMaps;
import tech.kayys.andalus.a2ui.andalus.http.HttpOperationalDiagnosticsProjection;
import tech.kayys.andalus.a2ui.andalus.transport.TransportJson;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;

import tech.kayys.andalus.a2ui.andalus.support.RecordValues;
import tech.kayys.andalus.a2ui.andalus.support.RecordNumbers;
import tech.kayys.andalus.a2ui.andalus.support.DecodeCollections;

import java.util.List;
import java.util.Map;

/**
 * Compact operator-facing summary for A2UI HTTP operational diagnostics.
 */
public record WayangA2uiHttpOperationalDiagnosticsSummary(
        String diagnosticsId,
        boolean passed,
        int exitCode,
        int issueCount,
        List<String> issueCodes,
        boolean bindingReportPassed,
        boolean actionBindingPassed,
        boolean smokeRequired,
        boolean smokePassed,
        int routeOperationCount,
        int routeHandlerOperationCount,
        int missingRouteHandlerCount,
        int orphanRouteHandlerCount,
        int policyActionCount,
        int actionHandlerCount,
        int missingActionHandlerCount,
        int orphanActionHandlerCount,
        int smokeScenarioCount,
        int smokeIssueCount,
        int smokeRouteCount,
        Map<String, Object> attributes) {

    public WayangA2uiHttpOperationalDiagnosticsSummary {
        diagnosticsId = RecordValues.textOrDefault(
                diagnosticsId,
                WayangA2uiHttpOperationalDiagnostics.DIAGNOSTICS_ID);
        exitCode = RecordNumbers.nonNegative(exitCode);
        issueCount = RecordNumbers.nonNegative(issueCount);
        issueCodes = DecodeCollections.distinctNonBlankTexts(issueCodes);
        routeOperationCount = RecordNumbers.nonNegative(routeOperationCount);
        routeHandlerOperationCount = RecordNumbers.nonNegative(routeHandlerOperationCount);
        missingRouteHandlerCount = RecordNumbers.nonNegative(missingRouteHandlerCount);
        orphanRouteHandlerCount = RecordNumbers.nonNegative(orphanRouteHandlerCount);
        policyActionCount = RecordNumbers.nonNegative(policyActionCount);
        actionHandlerCount = RecordNumbers.nonNegative(actionHandlerCount);
        missingActionHandlerCount = RecordNumbers.nonNegative(missingActionHandlerCount);
        orphanActionHandlerCount = RecordNumbers.nonNegative(orphanActionHandlerCount);
        smokeScenarioCount = RecordNumbers.nonNegative(smokeScenarioCount);
        smokeIssueCount = RecordNumbers.nonNegative(smokeIssueCount);
        smokeRouteCount = RecordNumbers.nonNegative(smokeRouteCount);
        attributes = TransportMaps.copy(attributes);
    }

    public static WayangA2uiHttpOperationalDiagnosticsSummary empty() {
        return from(WayangA2uiHttpOperationalDiagnostics.empty());
    }

    public static WayangA2uiHttpOperationalDiagnosticsSummary from(
            WayangA2uiHttpOperationalDiagnostics diagnostics) {
        if (diagnostics == null) {
            return empty();
        }
        WayangA2uiHttpOperationalDiagnostics resolved = diagnostics;
        WayangA2uiHttpBindingReportProbeResult binding = resolved.bindingReportProbe();
        WayangA2uiHttpActionBindingProbeResult actionBinding = resolved.actionBindingProbe();
        WayangA2uiHttpSmokeProbeResult smoke = resolved.smokeProbe();
        return new WayangA2uiHttpOperationalDiagnosticsSummary(
                WayangA2uiHttpOperationalDiagnostics.DIAGNOSTICS_ID,
                resolved.passed(),
                resolved.exitCode(),
                resolved.issueCount(),
                HttpIssueMaps.issueValues(resolved.issues(), "code"),
                resolved.bindingReportPassed(),
                resolved.actionBindingPassed(),
                resolved.smokeRequired(),
                resolved.smokePassed(),
                binding.routeOperationCount(),
                binding.handlerOperationCount(),
                binding.missingHandlerCount(),
                binding.orphanHandlerCount(),
                actionBinding.policyActionCount(),
                actionBinding.handlerActionCount(),
                actionBinding.missingHandlerCount(),
                actionBinding.orphanHandlerCount(),
                smoke.summary().scenarioCount(),
                smoke.summary().issueCount(),
                smoke.summary().routeCount(),
                Map.of("readinessId", WayangA2uiHttpReadinessProbeResult.READINESS_ID));
    }

    public static WayangA2uiHttpOperationalDiagnosticsSummary fromMap(Map<?, ?> values) {
        return WayangA2uiHttpOperationalDiagnosticsSummaryDecoder.fromMap(values);
    }

    public static WayangA2uiHttpOperationalDiagnosticsSummary fromJson(String json) {
        return WayangA2uiHttpOperationalDiagnosticsSummaryDecoder.fromJson(json);
    }

    public boolean successfulExit() {
        return passed && exitCode == WayangA2uiHttpSmokeResult.EXIT_SUCCESS;
    }

    public Map<String, Object> toMap() {
        return HttpOperationalDiagnosticsProjection.summary(this);
    }

    public String toJson() {
        return TransportJson.json(
                toMap(),
                "Unable to encode A2UI HTTP operational diagnostics summary");
    }
}
