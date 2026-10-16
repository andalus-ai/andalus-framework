package tech.kayys.andalus.a2ui.andalus.http;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiBridgeResponse;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiHttpEndpointBinding;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiHttpEndpointPublication;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiTransportResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpPublicationProjectionTest {

    @Test
    void projectsOrderedPublicationEnvelopeAndRecordDelegates() {
        WayangA2uiHttpEndpointPublication publication = endpoint().publication();

        Map<String, Object> values = HttpPublicationProjection.publication(publication);

        assertThat(publication.toMap()).isEqualTo(values);
        assertThat(values.keySet()).containsExactly(
                "routeCount",
                "operations",
                "paths",
                "routes");
        assertThat(values)
                .containsEntry("routeCount", 6)
                .containsEntry("operations", publication.operations())
                .containsEntry("paths", publication.paths());
        assertThat((Iterable<Map<String, Object>>) values.get("routes"))
                .allSatisfy(route -> assertThat(route).containsEntry("published", true));
    }

    private static WayangA2uiHttpEndpointBinding endpoint() {
        return new WayangA2uiHttpEndpointBinding(
                request -> WayangA2uiBridgeResponse.of(WayangA2uiTransportResponse.error("unused", "Unused")),
                "/api/a2ui");
    }
}
