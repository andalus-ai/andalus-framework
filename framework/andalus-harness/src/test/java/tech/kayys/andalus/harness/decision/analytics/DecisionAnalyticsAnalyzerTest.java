package tech.kayys.andalus.harness.decision.analytics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.decision.observability.DecisionObservabilityRecord;
import tech.kayys.andalus.harness.decision.observability.DecisionObservabilityRuntime;
import tech.kayys.andalus.harness.decision.observability.DecisionPayloadCaptureMode;
import tech.kayys.andalus.harness.decision.observability.DefaultDecisionObservabilityRuntime;
import tech.kayys.andalus.harness.observability.event.AndalusEvent;
import tech.kayys.andalus.harness.observability.event.EventBus;
import tech.kayys.andalus.harness.observability.event.EventFilter;
import tech.kayys.andalus.harness.observability.event.EventHandler;
import tech.kayys.andalus.harness.observability.event.EventSubscription;
import tech.kayys.andalus.harness.observability.metrics.MetricsRuntime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Phase 4O — Decision Analytics.
 */
class DecisionAnalyticsAnalyzerTest {

    private static final EventBus NOOP_BUS = new EventBus() {
        @Override public void publish(AndalusEvent e) {}
        @Override public EventSubscription subscribe(EventFilter f, EventHandler h) {
            return new EventSubscription() {
                @Override public boolean isActive() { return false; }
                @Override public void unsubscribe() {}
            };
        }
    };

    private DefaultDecisionObservabilityRuntime observability;
    private DefaultDecisionAnalyticsAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        observability = new DefaultDecisionObservabilityRuntime(
                true, DecisionPayloadCaptureMode.METADATA, NOOP_BUS, MetricsRuntime.inMemory());
        analyzer = new DefaultDecisionAnalyticsAnalyzer(observability);
    }

    // -------------------------------------------------------------------------
    // Query tests
    // -------------------------------------------------------------------------

    @Test
    void query_all_factoryDefaultsAreValid() {
        var q = DecisionAnalyticsQuery.all();
        assertThat(q.limit()).isEqualTo(100);
        assertThat(q.executionIds()).isEmpty();
        assertThat(q.governanceOutcomes()).isEmpty();
    }

    @Test
    void query_forExecution_scopesExecutionId() {
        var q = DecisionAnalyticsQuery.forExecution("exec-99");
        assertThat(q.executionIds()).containsExactly("exec-99");
    }

    @Test
    void query_forGovernanceOutcome_setsFilter() {
        var q = DecisionAnalyticsQuery.forGovernanceOutcome("DENY");
        assertThat(q.governanceOutcomes()).containsExactly("DENY");
    }

    // -------------------------------------------------------------------------
    // Report tests
    // -------------------------------------------------------------------------

    @Test
    void analyze_emptyResult_whenNoRecordsMatchExecution() throws Exception {
        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("unknown")).toCompletableFuture().get();
        assertThat(report.decisionsStarted()).isEqualTo(0);
        assertThat(report.dataComplete()).isTrue();
    }

    @Test
    void analyze_countsLifecycleEvents() throws Exception {
        record("exec-1", "DECISION_STARTED");
        record("exec-1", "DECISION_PRODUCED");
        record("exec-1", "DECISION_COMPLETED");

        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-1")).toCompletableFuture().get();
        assertThat(report.decisionsStarted()).isEqualTo(1);
        assertThat(report.decisionsCompleted()).isEqualTo(1);
        assertThat(report.decisionsFailed()).isEqualTo(0);
    }

    @Test
    void analyze_countsFailuresAndTimeouts() throws Exception {
        record("exec-2", "DECISION_STARTED");
        record("exec-2", "DECISION_FAILED");
        record("exec-2", "DECISION_TIMED_OUT");

        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-2")).toCompletableFuture().get();
        assertThat(report.decisionsFailed()).isEqualTo(1);
        assertThat(report.decisionsTimedOut()).isEqualTo(1);
    }

    @Test
    void analyze_countsCancellations() throws Exception {
        record("exec-3", "DECISION_CANCELLED");
        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-3")).toCompletableFuture().get();
        assertThat(report.decisionsCancelled()).isEqualTo(1);
    }

    @Test
    void analyze_countsGovernanceDenials() throws Exception {
        recordWithGovernance("exec-4", "DENY");
        recordWithGovernance("exec-4", "ALLOW");
        recordWithGovernance("exec-4", "DENY");

        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-4")).toCompletableFuture().get();
        assertThat(report.governanceDenials()).isEqualTo(2);
        assertThat(report.outcomesByGovernanceResult()).containsEntry("DENY", 2L);
        assertThat(report.outcomesByGovernanceResult()).containsEntry("ALLOW", 1L);
    }

    @Test
    void analyze_waitingDecisionsAreNotCountedAsFailures() throws Exception {
        record("exec-5", "DECISION_WAITING");
        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-5")).toCompletableFuture().get();
        assertThat(report.governanceWaiting()).isEqualTo(1);
        assertThat(report.decisionsFailed()).isEqualTo(0);
    }

    @Test
    void analyze_replanningRequestsAreCounted() throws Exception {
        record("exec-6", "DECISION_REPLANNING_REQUESTED");
        record("exec-6", "DECISION_REPLANNING_REQUESTED");
        var report = analyzer.analyze(DecisionAnalyticsQuery.forExecution("exec-6")).toCompletableFuture().get();
        assertThat(report.replanningRequests()).isEqualTo(2);
    }

    @Test
    void analyze_incompleteReport_whenObservabilityDisabled() throws Exception {
        var disabled = new DefaultDecisionAnalyticsAnalyzer(DecisionObservabilityRuntime.noop());
        var report = disabled.analyze(DecisionAnalyticsQuery.all()).toCompletableFuture().get();
        assertThat(report.dataComplete()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Findings tests
    // -------------------------------------------------------------------------

    @Test
    void findings_governanceDenialRate_isProduced() throws Exception {
        recordWithGovernance("exec-7", "DENY");
        recordWithGovernance("exec-7", "ALLOW");

        var fs = analyzer.findings(DecisionAnalyticsQuery.forExecution("exec-7")).toCompletableFuture().get();
        assertThat(fs).anyMatch(f -> f.metricId().equals("andalus_decision_governance_denial_rate"));
        var denial = fs.stream()
                .filter(f -> f.metricId().equals("andalus_decision_governance_denial_rate"))
                .findFirst().orElseThrow();
        assertThat(denial.population()).isEqualTo(1);
        assertThat(denial.denominator()).isEqualTo(2);
        assertThat(denial.rate()).contains(0.5);
    }

    @Test
    void findings_replanningFinding_producedWhenPresent() throws Exception {
        record("exec-8", "DECISION_STARTED");
        record("exec-8", "DECISION_REPLANNING_REQUESTED");

        var fs = analyzer.findings(DecisionAnalyticsQuery.forExecution("exec-8")).toCompletableFuture().get();
        assertThat(fs).anyMatch(f -> f.metricId().equals("andalus_decision_replanning_rate"));
    }

    @Test
    void findings_waitingFinding_producedWhenPresent() throws Exception {
        record("exec-9", "DECISION_STARTED");
        record("exec-9", "DECISION_WAITING");

        var fs = analyzer.findings(DecisionAnalyticsQuery.forExecution("exec-9")).toCompletableFuture().get();
        assertThat(fs).anyMatch(f -> f.metricId().equals("andalus_decision_waiting_total"));
    }

    @Test
    void findings_incomplete_returnsSingleFindingWhenObservabilityDisabled() throws Exception {
        var disabled = new DefaultDecisionAnalyticsAnalyzer(DecisionObservabilityRuntime.noop());
        var fs = disabled.findings(DecisionAnalyticsQuery.all()).toCompletableFuture().get();
        assertThat(fs).hasSize(1);
        assertThat(fs.get(0).dataComplete()).isFalse();
    }

    // -------------------------------------------------------------------------
    // DecisionAnalyticsFinding model tests
    // -------------------------------------------------------------------------

    @Test
    void finding_rate_isEmptyWhenDenominatorIsZero() {
        var f = DecisionAnalyticsFinding.incomplete("m1", "CAT", "desc", Map.of());
        assertThat(f.rate()).isEmpty();
        assertThat(f.dataComplete()).isFalse();
    }

    @Test
    void finding_rate_isComputedCorrectly() {
        var f = DecisionAnalyticsFinding.of("m2", "CAT", "desc", 3, 10, Map.of(), List.of());
        assertThat(f.rate()).contains(0.3);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void record(String executionId, String eventType) {
        observability.record(DecisionObservabilityRecord.metadata(
                executionId, "turn-1", "trace-1", "dt-1", eventType));
    }

    private void recordWithGovernance(String executionId, String outcome) {
        observability.record(new DecisionObservabilityRecord(
                null, executionId, "turn-1", "trace-1", "dt-gov",
                "DECISION_GOVERNANCE_COMPLETED", null,
                Optional.empty(),
                Optional.of(outcome),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()));
    }
}
