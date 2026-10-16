package tech.kayys.andalus.a2ui.andalus;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.andalus.action.ActionBindingReportProjection;
import tech.kayys.andalus.a2ui.andalus.action.ActionContextReader;
import tech.kayys.andalus.a2ui.andalus.action.ActionGate;
import tech.kayys.andalus.a2ui.andalus.action.ActionMetadata;
import tech.kayys.andalus.a2ui.andalus.action.ActionQueries;
import tech.kayys.andalus.a2ui.andalus.action.ActionResponses;
import tech.kayys.andalus.a2ui.andalus.http.HttpActionBindingProbeProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpActionBindingProbeResponseDecoder;
import tech.kayys.andalus.a2ui.andalus.http.HttpBindingReportProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpBindingReportProbeProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpBindingReportProbeResponseDecoder;
import tech.kayys.andalus.a2ui.andalus.http.HttpEndpointDiagnosticPlanProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpEndpointDiagnosticProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpEndpointProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpExpectationProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpExchangeMetrics;
import tech.kayys.andalus.a2ui.andalus.http.HttpHeaderValues;
import tech.kayys.andalus.a2ui.andalus.http.HttpIssueMaps;
import tech.kayys.andalus.a2ui.andalus.http.HttpMetricExchange;
import tech.kayys.andalus.a2ui.andalus.http.HttpOperationalDiagnosticsProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpPublicationProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpReadinessProbeProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpReadinessProbeResponseDecoder;
import tech.kayys.andalus.a2ui.andalus.http.HttpReportMetricDecoders;
import tech.kayys.andalus.a2ui.andalus.http.HttpReportMetrics;
import tech.kayys.andalus.a2ui.andalus.http.HttpResponseBodyDecoder;
import tech.kayys.andalus.a2ui.andalus.http.HttpRouteProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpScenarioProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpScenarioSuiteMetrics;
import tech.kayys.andalus.a2ui.andalus.http.HttpSmokeProbeProjection;
import tech.kayys.andalus.a2ui.andalus.http.HttpSmokeProbeResponseDecoder;
import tech.kayys.andalus.a2ui.andalus.http.HttpSmokeResultProjection;
import tech.kayys.andalus.a2ui.andalus.projection.SessionProjection;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigDecoder;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigLookupProvider;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigLoadAttempt;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigLoadResult;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigLoadResultDecoder;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigLoadStatus;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigObjectStorageProvider;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigRequestContext;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigRequestDiagnostics;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigRequestDiagnosticsDecoder;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigRequestDiagnosticsSummary;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigRequestDiagnosticsSummaryDecoder;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSource;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceCapability;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceDiagnostics;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceDiagnosticsDecoder;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourcePolicy;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceProvider;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceRedactor;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceRegistries;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceRegistry;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceSpec;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSourceSpecs;
import tech.kayys.andalus.a2ui.andalus.session.SessionConfigSources;
import tech.kayys.andalus.a2ui.andalus.session.SessionProfiles;
import tech.kayys.andalus.a2ui.andalus.surface.RunActionControls;
import tech.kayys.andalus.a2ui.andalus.surface.RunDataModels;
import tech.kayys.andalus.a2ui.andalus.surface.RunEventsSurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunHistorySurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunInspectionSurface;
import tech.kayys.andalus.a2ui.andalus.surface.RunStatusSurface;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceActions;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceData;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceIds;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceLayouts;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceMessages;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceProjection;
import tech.kayys.andalus.a2ui.andalus.surface.SurfaceText;
import tech.kayys.andalus.a2ui.andalus.transport.TransportJson;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMetadataProjection;
import tech.kayys.andalus.a2ui.andalus.transport.TransportExchangeMetrics;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMetricExchange;
import tech.kayys.andalus.a2ui.andalus.transport.TransportProjection;

import static org.assertj.core.api.Assertions.assertThat;

class WayangA2uiModuleBoundaryTest {

    @Test
    void namesTargetPackageSkeleton() {
        assertThat(WayangA2uiModuleBoundary.basePackage())
                .isEqualTo("tech.kayys.andalus.a2ui.andalus");
        assertThat(WayangA2uiModuleBoundary.targetSubpackages())
                .extracting(WayangA2uiModuleBoundary::packageName)
                .containsExactly(
                        "tech.kayys.andalus.a2ui.andalus.action",
                        "tech.kayys.andalus.a2ui.andalus.bridge",
                        "tech.kayys.andalus.a2ui.andalus.http",
                        "tech.kayys.andalus.a2ui.andalus.projection",
                        "tech.kayys.andalus.a2ui.andalus.session",
                        "tech.kayys.andalus.a2ui.andalus.spec",
                        "tech.kayys.andalus.a2ui.andalus.surface",
                        "tech.kayys.andalus.a2ui.andalus.transport",
                        "tech.kayys.andalus.a2ui.andalus.support");
    }

    @Test
    void exposesUniqueBoundaryLabelsAndResponsibilities() {
        assertThat(WayangA2uiModuleBoundary.values())
                .extracting(WayangA2uiModuleBoundary::label)
                .doesNotHaveDuplicates();
        assertThat(WayangA2uiModuleBoundary.values())
                .allSatisfy(boundary -> assertThat(boundary.responsibility()).isNotBlank());
        assertThat(WayangA2uiModuleBoundary.FACADE.rootPackage()).isTrue();
        assertThat(WayangA2uiModuleBoundary.HTTP.targetSubpackage()).isTrue();
    }

    @Test
    void publishesPackageDescriptorsForEachBoundary() {
        assertThat(WayangA2uiModuleBoundary.values())
                .allSatisfy(boundary -> assertThat(packageDescriptor(boundary))
                        .as(boundary.packageName())
                        .isNotNull());
    }

    @Test
    void keepsPublicApiBrandedAndLocalHelpersCompact() {
        assertThat(WayangA2ui.class.getSimpleName()).startsWith("WayangA2ui");
        assertThat(ActionBindingReportProjection.class.getSimpleName())
                .isEqualTo("ActionBindingReportProjection");
        assertThat(ActionContextReader.class.getSimpleName()).isEqualTo("ActionContextReader");
        assertThat(ActionGate.class.getSimpleName()).isEqualTo("ActionGate");
        assertThat(ActionMetadata.class.getSimpleName()).isEqualTo("ActionMetadata");
        assertThat(ActionQueries.class.getSimpleName()).isEqualTo("ActionQueries");
        assertThat(ActionResponses.class.getSimpleName()).isEqualTo("ActionResponses");
        assertThat(HttpActionBindingProbeProjection.class.getSimpleName())
                .isEqualTo("HttpActionBindingProbeProjection");
        assertThat(HttpActionBindingProbeResponseDecoder.class.getSimpleName())
                .isEqualTo("HttpActionBindingProbeResponseDecoder");
        assertThat(HttpBindingReportProjection.class.getSimpleName()).isEqualTo("HttpBindingReportProjection");
        assertThat(HttpBindingReportProbeProjection.class.getSimpleName())
                .isEqualTo("HttpBindingReportProbeProjection");
        assertThat(HttpBindingReportProbeResponseDecoder.class.getSimpleName())
                .isEqualTo("HttpBindingReportProbeResponseDecoder");
        assertThat(HttpEndpointDiagnosticPlanProjection.class.getSimpleName())
                .isEqualTo("HttpEndpointDiagnosticPlanProjection");
        assertThat(HttpEndpointDiagnosticProjection.class.getSimpleName())
                .isEqualTo("HttpEndpointDiagnosticProjection");
        assertThat(HttpEndpointProjection.class.getSimpleName()).isEqualTo("HttpEndpointProjection");
        assertThat(HttpExpectationProjection.class.getSimpleName()).isEqualTo("HttpExpectationProjection");
        assertThat(HttpExchangeMetrics.class.getSimpleName()).isEqualTo("HttpExchangeMetrics");
        assertThat(HttpHeaderValues.class.getSimpleName()).isEqualTo("HttpHeaderValues");
        assertThat(HttpMetricExchange.class.getSimpleName()).isEqualTo("HttpMetricExchange");
        assertThat(HttpOperationalDiagnosticsProjection.class.getSimpleName())
                .isEqualTo("HttpOperationalDiagnosticsProjection");
        assertThat(HttpPublicationProjection.class.getSimpleName()).isEqualTo("HttpPublicationProjection");
        assertThat(HttpReadinessProbeProjection.class.getSimpleName())
                .isEqualTo("HttpReadinessProbeProjection");
        assertThat(HttpReadinessProbeResponseDecoder.class.getSimpleName())
                .isEqualTo("HttpReadinessProbeResponseDecoder");
        assertThat(HttpReportMetricDecoders.class.getSimpleName()).isEqualTo("HttpReportMetricDecoders");
        assertThat(HttpReportMetrics.class.getSimpleName()).isEqualTo("HttpReportMetrics");
        assertThat(HttpResponseBodyDecoder.class.getSimpleName()).isEqualTo("HttpResponseBodyDecoder");
        assertThat(HttpRouteProjection.class.getSimpleName()).isEqualTo("HttpRouteProjection");
        assertThat(HttpScenarioProjection.class.getSimpleName()).isEqualTo("HttpScenarioProjection");
        assertThat(HttpScenarioSuiteMetrics.class.getSimpleName()).isEqualTo("HttpScenarioSuiteMetrics");
        assertThat(HttpSmokeProbeProjection.class.getSimpleName()).isEqualTo("HttpSmokeProbeProjection");
        assertThat(HttpSmokeProbeResponseDecoder.class.getSimpleName()).isEqualTo("HttpSmokeProbeResponseDecoder");
        assertThat(HttpSmokeResultProjection.class.getSimpleName()).isEqualTo("HttpSmokeResultProjection");
        assertThat(TransportMaps.class.getSimpleName()).isEqualTo("TransportMaps");
        assertThat(TransportJson.class.getSimpleName()).isEqualTo("TransportJson");
        assertThat(TransportExchangeMetrics.class.getSimpleName()).isEqualTo("TransportExchangeMetrics");
        assertThat(TransportMetricExchange.class.getSimpleName()).isEqualTo("TransportMetricExchange");
        assertThat(TransportMetadataProjection.class.getSimpleName()).isEqualTo("TransportMetadataProjection");
        assertThat(TransportProjection.class.getSimpleName()).isEqualTo("TransportProjection");
        assertThat(HttpIssueMaps.class.getSimpleName()).isEqualTo("HttpIssueMaps");
        assertThat(SessionProjection.class.getSimpleName()).isEqualTo("SessionProjection");
        assertThat(SessionConfigDecoder.class.getSimpleName()).isEqualTo("SessionConfigDecoder");
        assertThat(SessionConfigLookupProvider.class.getSimpleName()).isEqualTo("SessionConfigLookupProvider");
        assertThat(SessionConfigLoadAttempt.class.getSimpleName()).isEqualTo("SessionConfigLoadAttempt");
        assertThat(SessionConfigLoadResult.class.getSimpleName()).isEqualTo("SessionConfigLoadResult");
        assertThat(SessionConfigLoadResultDecoder.class.getSimpleName()).isEqualTo("SessionConfigLoadResultDecoder");
        assertThat(SessionConfigLoadStatus.class.getSimpleName()).isEqualTo("SessionConfigLoadStatus");
        assertThat(SessionConfigObjectStorageProvider.class.getSimpleName())
                .isEqualTo("SessionConfigObjectStorageProvider");
        assertThat(SessionConfigRequestContext.class.getSimpleName()).isEqualTo("SessionConfigRequestContext");
        assertThat(SessionConfigRequestDiagnostics.class.getSimpleName())
                .isEqualTo("SessionConfigRequestDiagnostics");
        assertThat(SessionConfigRequestDiagnosticsDecoder.class.getSimpleName())
                .isEqualTo("SessionConfigRequestDiagnosticsDecoder");
        assertThat(SessionConfigRequestDiagnosticsSummary.class.getSimpleName())
                .isEqualTo("SessionConfigRequestDiagnosticsSummary");
        assertThat(SessionConfigRequestDiagnosticsSummaryDecoder.class.getSimpleName())
                .isEqualTo("SessionConfigRequestDiagnosticsSummaryDecoder");
        assertThat(SessionConfigSource.class.getSimpleName()).isEqualTo("SessionConfigSource");
        assertThat(SessionConfigSourceCapability.class.getSimpleName()).isEqualTo("SessionConfigSourceCapability");
        assertThat(SessionConfigSourceDiagnostics.class.getSimpleName())
                .isEqualTo("SessionConfigSourceDiagnostics");
        assertThat(SessionConfigSourceDiagnosticsDecoder.class.getSimpleName())
                .isEqualTo("SessionConfigSourceDiagnosticsDecoder");
        assertThat(SessionConfigSourcePolicy.class.getSimpleName()).isEqualTo("SessionConfigSourcePolicy");
        assertThat(SessionConfigSourceProvider.class.getSimpleName()).isEqualTo("SessionConfigSourceProvider");
        assertThat(SessionConfigSourceRedactor.class.getSimpleName()).isEqualTo("SessionConfigSourceRedactor");
        assertThat(SessionConfigSourceRegistries.class.getSimpleName()).isEqualTo("SessionConfigSourceRegistries");
        assertThat(SessionConfigSourceRegistry.class.getSimpleName()).isEqualTo("SessionConfigSourceRegistry");
        assertThat(SessionConfigSourceSpec.class.getSimpleName()).isEqualTo("SessionConfigSourceSpec");
        assertThat(SessionConfigSourceSpecs.class.getSimpleName()).isEqualTo("SessionConfigSourceSpecs");
        assertThat(SessionConfigSources.class.getSimpleName()).isEqualTo("SessionConfigSources");
        assertThat(SessionProfiles.class.getSimpleName()).isEqualTo("SessionProfiles");
        assertThat(RunActionControls.class.getSimpleName()).isEqualTo("RunActionControls");
        assertThat(RunDataModels.class.getSimpleName()).isEqualTo("RunDataModels");
        assertThat(RunEventsSurface.class.getSimpleName()).isEqualTo("RunEventsSurface");
        assertThat(RunHistorySurface.class.getSimpleName()).isEqualTo("RunHistorySurface");
        assertThat(RunInspectionSurface.class.getSimpleName()).isEqualTo("RunInspectionSurface");
        assertThat(RunStatusSurface.class.getSimpleName()).isEqualTo("RunStatusSurface");
        assertThat(SurfaceActions.class.getSimpleName()).isEqualTo("SurfaceActions");
        assertThat(SurfaceData.class.getSimpleName()).isEqualTo("SurfaceData");
        assertThat(SurfaceIds.class.getSimpleName()).isEqualTo("SurfaceIds");
        assertThat(SurfaceLayouts.class.getSimpleName()).isEqualTo("SurfaceLayouts");
        assertThat(SurfaceMessages.class.getSimpleName()).isEqualTo("SurfaceMessages");
        assertThat(SurfaceProjection.class.getSimpleName()).isEqualTo("SurfaceProjection");
        assertThat(SurfaceText.class.getSimpleName()).isEqualTo("SurfaceText");
    }

    private static Class<?> packageDescriptor(WayangA2uiModuleBoundary boundary) {
        try {
            return Class.forName(boundary.packageName() + ".package-info");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
