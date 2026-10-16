package tech.kayys.andalus.a2ui.andalus;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.a2ui.core.A2uiProtocol;
import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentHealthReport;
import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentPolicyConfig;
import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentPortfolio;
import tech.kayys.andalus.aqli.sdk.WayangStandardAlignmentProvider;

import java.util.List;
import java.util.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;

class WayangA2uiStandardAlignmentTest {

    @Test
    void adaptsNativeA2uiReportToSdkPortfolioAndPinnedHealth() {
        WayangStandardAlignmentPortfolio portfolio = WayangA2uiStandardAlignment.portfolio();
        WayangStandardAlignmentHealthReport health = WayangA2uiStandardAlignment.pinnedHealth();

        assertThat(portfolio.standardIds()).containsExactly("a2ui");
        assertThat(portfolio.aligned()).isTrue();
        assertThat(portfolio.registryDrift().driftFree()).isTrue();
        assertThat(health.ready()).isTrue();
        assertThat(health.policyAssessment().requiredStandardIds()).containsExactly("a2ui");
        assertThat(health.policyAssessment().requiredVersions()).containsEntry("a2ui", A2uiProtocol.VERSION);
    }

    @Test
    void nullReportFallbackUsesCanonicalDefaultReport() {
        assertThat(WayangA2uiStandardAlignment.portfolio(null).toMap())
                .isEqualTo(WayangA2uiStandardAlignment.portfolio(
                        WayangA2uiSpecAlignmentReport.defaultReport()).toMap());
        assertThat(WayangA2uiStandardAlignment.pinnedHealth(null).ready()).isTrue();
    }

    @Test
    void configuredHealthPreservesAlignmentGaps() {
        WayangA2uiSpecAlignmentReport report =
                WayangA2uiSpecAlignmentReport.from(new WayangA2uiHttpRouteCatalog(List.of()));

        WayangStandardAlignmentHealthReport health = WayangA2uiStandardAlignment.health(
                report,
                WayangStandardAlignmentPolicyConfig.pinnedRegistry("agent-to-user-interface"));

        assertThat(health.ready()).isFalse();
        assertThat(health.policyAssessment().failingStandardIds()).containsExactly("a2ui");
        assertThat(health.portfolio().gapStandardIds()).containsExactly("a2ui");
    }

    @Test
    void registersStandardAlignmentProviderForServiceDiscovery() {
        assertThat(ServiceLoader.load(WayangStandardAlignmentProvider.class))
                .anySatisfy(provider -> {
                    assertThat(provider).isInstanceOf(WayangA2uiStandardAlignmentProvider.class);
                    assertThat(provider.providerId()).isEqualTo("andalus-a2ui-standard-alignment");
                    assertThat(provider.portfolio().standardIds()).containsExactly("a2ui");
                });
    }
}
