package tech.kayys.andalus.a2ui.andalus.surface;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurfaceIdsTest {

    @Test
    void normalizesSafeSurfaceIdSegments() {
        assertThat(SurfaceIds.safe(" RUN 123 ")).isEqualTo("run-123");
        assertThat(SurfaceIds.safe("andalus.run.cancel")).isEqualTo("andalus-run-cancel");
        assertThat(SurfaceIds.safe(" ")).isEqualTo("unknown");
        assertThat(SurfaceIds.safe(null)).isEqualTo("unknown");
        assertThat(SurfaceIds.safe(" ", "action")).isEqualTo("action");
        assertThat(SurfaceIds.safe(" ", " ")).isEqualTo("unknown");
    }

    @Test
    void buildsStandardSurfaceIds() {
        assertThat(SurfaceIds.runStatus("RUN 123")).isEqualTo("andalus-run-run-123");
        assertThat(SurfaceIds.runEvents("run-1")).isEqualTo("andalus-run-events-run-1");
        assertThat(SurfaceIds.runHistory()).isEqualTo("andalus-run-history");
        assertThat(SurfaceIds.runHistoryRow("run-1")).isEqualTo("andalus-run-history-run-run-1");
    }

    @Test
    void buildsActionResultSurfaceIds() {
        assertThat(SurfaceIds.actionResult(7, "custom.action"))
                .isEqualTo("andalus-action-result-7-custom-action");
        assertThat(SurfaceIds.actionResult(1, " "))
                .isEqualTo("andalus-action-result-1-action");
    }
}
