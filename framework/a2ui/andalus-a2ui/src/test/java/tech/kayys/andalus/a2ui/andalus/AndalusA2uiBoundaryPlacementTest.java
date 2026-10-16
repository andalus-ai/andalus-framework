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
import tech.kayys.andalus.a2ui.andalus.projection.SpecAlignmentProjection;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WayangA2uiBoundaryPlacementTest {

    @Test
    void namesSupportFirstMigrationCandidates() {
        assertThat(WayangA2uiBoundaryPlacement.supportFirstCandidates())
                .containsExactly(
                        "DecodeCollections",
                        "DecodeValues",
                        "ProjectionCollections",
                        "RecordCollections",
                        "RecordMaps",
                        "RecordNumbers",
                        "RecordValues",
                        "StringMaps");
        assertThat(WayangA2uiBoundaryPlacement.supportFirstCandidates())
                .allSatisfy(candidate -> assertThat(WayangA2uiBoundaryPlacement.classifySimpleName(candidate))
                        .isEqualTo(WayangA2uiModuleBoundary.SUPPORT));
    }

    @Test
    void classifiesProjectionHelpersBeforeDomainWords() {
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.PROJECTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SpecAlignmentProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.PROJECTION);
        assertThat(WayangA2uiBoundaryPlacement.classifySimpleName("ProjectionMaps"))
                .isEqualTo(WayangA2uiModuleBoundary.PROJECTION);
    }

    @Test
    void classifiesRepresentativePublicContracts() {
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2ui.class))
                .isEqualTo(WayangA2uiModuleBoundary.FACADE);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiActionPolicy.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionBindingReportProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionContextReader.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionGate.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionMetadata.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionQueries.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(ActionResponses.class))
                .isEqualTo(WayangA2uiModuleBoundary.ACTION);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiBridgeRequest.class))
                .isEqualTo(WayangA2uiModuleBoundary.BRIDGE);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiHttpScenarioSuite.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiSessionConfig.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigLookupProvider.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigLoadAttempt.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigLoadResult.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigLoadResultDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigLoadStatus.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigObjectStorageProvider.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigRequestContext.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigRequestDiagnostics.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigRequestDiagnosticsDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigRequestDiagnosticsSummary.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigRequestDiagnosticsSummaryDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSource.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceCapability.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceDiagnostics.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceDiagnosticsDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourcePolicy.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceProvider.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceRedactor.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceRegistries.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceRegistry.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceSpec.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSourceSpecs.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionConfigSources.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(SessionProfiles.class))
                .isEqualTo(WayangA2uiModuleBoundary.SESSION);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiSpecAlignmentReport.class))
                .isEqualTo(WayangA2uiModuleBoundary.SPEC);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiSurfaceCatalog.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunActionControls.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunDataModels.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunEventsSurface.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunHistorySurface.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunInspectionSurface.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(RunStatusSurface.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceActions.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceData.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceIds.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceLayouts.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceMessages.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(SurfaceText.class))
                .isEqualTo(WayangA2uiModuleBoundary.SURFACE);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiTransportRequest.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
    }

    @Test
    void keepsHttpModelsInsideHttpEvenWhenNamesMentionActionOrTransport() {
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiHttpActionBindingProbeResult.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(WayangA2uiHttpResponse.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpActionBindingProbeProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpActionBindingProbeResponseDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpBindingReportProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpBindingReportProbeProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpBindingReportProbeResponseDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpEndpointDiagnosticPlanProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpEndpointDiagnosticProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpEndpointProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpExpectationProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpExchangeMetrics.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpHeaderValues.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpIssueMaps.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpMetricExchange.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpOperationalDiagnosticsProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpPublicationProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpReadinessProbeProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpReadinessProbeResponseDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpReportMetricDecoders.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpReportMetrics.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpResponseBodyDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpRouteProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpScenarioProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpScenarioSuiteMetrics.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpSmokeProbeProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpSmokeProbeResponseDecoder.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
        assertThat(WayangA2uiBoundaryPlacement.classify(HttpSmokeResultProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.HTTP);
    }

    @Test
    void classifiesMovedTransportHelpersFromTheTransportPackage() {
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportMaps.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportJson.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportExchangeMetrics.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportMetricExchange.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportMetadataProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
        assertThat(WayangA2uiBoundaryPlacement.classify(TransportProjection.class))
                .isEqualTo(WayangA2uiModuleBoundary.TRANSPORT);
    }

    @Test
    void rejectsUnknownOrBlankClassNames() {
        assertThatThrownBy(() -> WayangA2uiBoundaryPlacement.classifySimpleName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
        assertThatThrownBy(() -> WayangA2uiBoundaryPlacement.classifySimpleName("UnknownHelper"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No A2UI module boundary placement");
    }
}
