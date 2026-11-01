package tech.kayys.andalus.harness.evaluation;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

/** Pluggable evaluator. Nested contracts form a small, dependency-free evaluation API. */
@FunctionalInterface
public interface Evaluator {
    CompletionStage<CaseResult> evaluate(EvaluationCase item);

    record EvaluationCase(String id, String input, String expectedOutput, Map<String,String> metadata) {
        public EvaluationCase {
            if (id == null || id.isBlank() || input == null) throw new IllegalArgumentException("case id and input are required");
            metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        }
    }
    record Dataset(String id, String version, List<EvaluationCase> cases) {
        public Dataset {
            if (id == null || id.isBlank() || version == null || version.isBlank()) throw new IllegalArgumentException("dataset id and version are required");
            cases = cases == null ? List.of() : List.copyOf(cases);
            if (cases.isEmpty()) throw new IllegalArgumentException("dataset must not be empty");
            Set<String> ids = new HashSet<>();
            for (EvaluationCase item : cases) if (item == null || !ids.add(item.id())) throw new IllegalArgumentException("null or duplicate evaluation case");
        }
    }
    record EvaluatorDescriptor(String id, String version, Map<String,String> configuration) {
        public EvaluatorDescriptor {
            if (id == null || id.isBlank() || version == null || version.isBlank())
                throw new IllegalArgumentException("evaluator id and version are required");
            configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
        }
        /** Stable SHA-256 over sorted key/value pairs; independent of map iteration order. */
        public String configurationFingerprint() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                new TreeMap<>(configuration).forEach((key,value) -> {
                    digest.update(key.getBytes(StandardCharsets.UTF_8));
                    digest.update((byte) 0);
                    digest.update(value.getBytes(StandardCharsets.UTF_8));
                    digest.update((byte) 0xff);
                });
                return HexFormat.of().formatHex(digest.digest());
            } catch (NoSuchAlgorithmException impossible) {
                throw new IllegalStateException("SHA-256 is unavailable", impossible);
            }
        }
    }
    record Request(String evaluationId, String candidateId, String candidateVersion, Dataset dataset,
                   EvaluatorDescriptor evaluator) {
        public Request(String evaluationId, String candidateId, String candidateVersion, Dataset dataset) {
            this(evaluationId, candidateId, candidateVersion, dataset,
                    new EvaluatorDescriptor("unspecified", "unspecified", Map.of()));
        }
        public Request {
            if (evaluationId == null || evaluationId.isBlank() || candidateId == null || candidateId.isBlank()
                    || candidateVersion == null || candidateVersion.isBlank() || dataset == null || evaluator == null)
                throw new IllegalArgumentException("evaluation identity, candidate version and dataset are required");
        }
    }
    record CaseResult(String caseId, Map<String,Double> metrics, boolean passed, String details) {
        public CaseResult {
            if (caseId == null || caseId.isBlank()) throw new IllegalArgumentException("caseId is required");
            metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
            metrics.forEach((k,v) -> { if (k == null || k.isBlank() || v == null || !Double.isFinite(v)) throw new IllegalArgumentException("metrics must be named and finite"); });
        }
    }
    record Report(String evaluationId, String candidateId, String candidateVersion, String datasetId,
                  String datasetVersion, Instant startedAt, Instant completedAt,
                  List<CaseResult> results, Map<String,Double> aggregateMetrics,
                  String evaluatorId, String evaluatorVersion, String evaluatorConfigFingerprint) {
        public Report(String evaluationId, String candidateId, String candidateVersion, String datasetId,
                      String datasetVersion, Instant startedAt, Instant completedAt,
                      List<CaseResult> results, Map<String,Double> aggregateMetrics) {
            this(evaluationId, candidateId, candidateVersion, datasetId, datasetVersion, startedAt, completedAt,
                    results, aggregateMetrics, "unspecified", "unspecified", "unspecified");
        }
        public Report {
            if (evaluationId == null || candidateId == null || candidateVersion == null || datasetId == null || datasetVersion == null\n                    || evaluatorId == null || evaluatorId.isBlank() || evaluatorVersion == null || evaluatorVersion.isBlank()\n                    || evaluatorConfigFingerprint == null || evaluatorConfigFingerprint.isBlank())
                throw new IllegalArgumentException("evaluation provenance is required");
            if (startedAt == null || completedAt == null || completedAt.isBefore(startedAt)) throw new IllegalArgumentException("invalid timestamps");
            results = results == null ? List.of() : List.copyOf(results);
            aggregateMetrics = aggregateMetrics == null ? Map.of() : Map.copyOf(aggregateMetrics);
        }
        public long passedCases() { return results.stream().filter(CaseResult::passed).count(); }
        public long failedCases() { return results.size() - passedCases(); }
        public double passRate() { return results.isEmpty() ? 0 : (double) passedCases() / results.size(); }
    }
    interface Runtime { CompletionStage<Report> evaluate(Request request); }

    /** Executes all cases, validates result identity, and averages each emitted metric independently. */
    final class DefaultRuntime implements Runtime {
        private final Evaluator evaluator;
        public DefaultRuntime(Evaluator evaluator) {
            if (evaluator == null) throw new IllegalArgumentException("evaluator is required");
            this.evaluator = evaluator;
        }
        public CompletionStage<Report> evaluate(Request request) {
            if (request == null) return CompletableFuture.failedFuture(new IllegalArgumentException("request is required"));
            Instant started = Instant.now();
            List<CompletableFuture<CaseResult>> tasks = new ArrayList<>();
            try {
                for (EvaluationCase item : request.dataset().cases()) {
                    CompletionStage<CaseResult> stage = evaluator.evaluate(item);
                    if (stage == null) throw new IllegalStateException("null evaluator stage: " + item.id());
                    tasks.add(stage.thenApply(result -> {
                        if (result == null || !item.id().equals(result.caseId())) throw new IllegalStateException("missing or mismatched result: " + item.id());
                        return result;
                    }).toCompletableFuture());
                }
            } catch (Throwable error) { return CompletableFuture.failedFuture(error); }
            return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new)).thenApply(ignored -> {
                List<CaseResult> results = tasks.stream().map(CompletableFuture::join).toList();
                Map<String,double[]> totals = new LinkedHashMap<>();
                results.forEach(result -> result.metrics().forEach((name,value) -> {
                    double[] a = totals.computeIfAbsent(name, k -> new double[2]); a[0] += value; a[1]++;
                }));
                Map<String,Double> means = new LinkedHashMap<>();
                totals.forEach((name,a) -> means.put(name, a[0] / a[1]));
                return new Report(request.evaluationId(), request.candidateId(), request.candidateVersion(),
                        request.dataset().id(), request.dataset().version(), started, Instant.now(), results, means,\n                        request.evaluator().id(), request.evaluator().version(), request.evaluator().configurationFingerprint());
            });
        }
    }
    enum Direction { AT_LEAST, AT_MOST }
    record MetricThreshold(String metric, Direction direction, double value) {
        public MetricThreshold {
            if (metric == null || metric.isBlank() || direction == null || !Double.isFinite(value))
                throw new IllegalArgumentException("valid metric threshold is required");
        }
    }
    record PromotionDecision(boolean approved, List<String> reasons) {
        public PromotionDecision { reasons = reasons == null ? List.of() : List.copyOf(reasons); }
    }
    /** Offline approval only: this gate never activates or deploys a candidate. */
    record PromotionGate(double minimumPassRate, List<MetricThreshold> thresholds) {
        public PromotionGate {
            if (!Double.isFinite(minimumPassRate) || minimumPassRate < 0 || minimumPassRate > 1)
                throw new IllegalArgumentException("minimumPassRate must be within [0,1]");
            thresholds = thresholds == null ? List.of() : List.copyOf(thresholds);
        }
        public PromotionDecision assess(Report report) {
            if (report == null) return new PromotionDecision(false, List.of("report is required"));
            List<String> reasons = new ArrayList<>();
            if (report.results().isEmpty()) reasons.add("evaluation has no case results");
            if (report.passRate() < minimumPassRate) reasons.add("pass rate below minimum: " + minimumPassRate);
            for (MetricThreshold t : thresholds) {
                Double actual = report.aggregateMetrics().get(t.metric());
                if (actual == null) reasons.add("required metric missing: " + t.metric());
                else if (t.direction() == Direction.AT_LEAST && actual < t.value()) reasons.add(t.metric() + " below minimum " + t.value());
                else if (t.direction() == Direction.AT_MOST && actual > t.value()) reasons.add(t.metric() + " above maximum " + t.value());
            }
            return new PromotionDecision(reasons.isEmpty(), reasons);
        }
    }

    /** Persistence boundary for durable evaluation provenance. Implementations may use SQL, files, or remote stores. */
    interface ReportStore {
        CompletionStage<Void> save(Report report);
        CompletionStage<Optional<Report>> findByEvaluationId(String evaluationId);
    }

    /** Small process-local implementation for tests and embedded deployments; not durable across restarts. */
    final class InMemoryReportStore implements ReportStore {
        private final ConcurrentMap<String, Report> reports = new ConcurrentHashMap<>();
        public CompletionStage<Void> save(Report report) {
            if (report == null) return CompletableFuture.failedFuture(new IllegalArgumentException("report is required"));
            Report existing = reports.putIfAbsent(report.evaluationId(), report);
            if (existing != null && !existing.equals(report))
                return CompletableFuture.failedFuture(new IllegalStateException("evaluation id already stores a different report: " + report.evaluationId()));
            return CompletableFuture.completedFuture(null);
        }
        public CompletionStage<Optional<Report>> findByEvaluationId(String evaluationId) {
            if (evaluationId == null || evaluationId.isBlank())
                return CompletableFuture.failedFuture(new IllegalArgumentException("evaluationId is required"));
            return CompletableFuture.completedFuture(Optional.ofNullable(reports.get(evaluationId)));
        }
    }

    record RegressionThreshold(String metric, double maximumRegression) {
        public RegressionThreshold {
            if (metric == null || metric.isBlank() || !Double.isFinite(maximumRegression) || maximumRegression < 0)
                throw new IllegalArgumentException("metric and non-negative finite regression tolerance are required");
        }
    }
    record Comparison(Report baseline, Report candidate, Map<String,Double> metricDeltas,
                      double passRateDelta, boolean regressed, List<String> reasons) {
        public Comparison {
            metricDeltas = metricDeltas == null ? Map.of() : Map.copyOf(metricDeltas);
            reasons = reasons == null ? List.of() : List.copyOf(reasons);
        }
    }

    /** Compares only reports evaluated against the exact same dataset revision. */
    final class RegressionAnalyzer {
        private final double maximumPassRateRegression;
        private final List<RegressionThreshold> thresholds;
        public RegressionAnalyzer(double maximumPassRateRegression, List<RegressionThreshold> thresholds) {
            if (!Double.isFinite(maximumPassRateRegression) || maximumPassRateRegression < 0 || maximumPassRateRegression > 1)
                throw new IllegalArgumentException("maximumPassRateRegression must be within [0,1]");
            this.maximumPassRateRegression = maximumPassRateRegression;
            this.thresholds = thresholds == null ? List.of() : List.copyOf(thresholds);
        }
        public Comparison compare(Report baseline, Report candidate) {
            if (baseline == null || candidate == null) throw new IllegalArgumentException("baseline and candidate reports are required");
            if (!baseline.datasetId().equals(candidate.datasetId()) || !baseline.datasetVersion().equals(candidate.datasetVersion()))
                throw new IllegalArgumentException("reports must use the same dataset id and version");
            Map<String,Double> deltas = new LinkedHashMap<>();
            candidate.aggregateMetrics().forEach((name,value) -> {
                Double old = baseline.aggregateMetrics().get(name);
                if (old != null) deltas.put(name, value - old);
            });
            double passDelta = candidate.passRate() - baseline.passRate();
            List<String> reasons = new ArrayList<>();
            if (passDelta < -maximumPassRateRegression)
                reasons.add("pass rate regression exceeds tolerance: " + passDelta);
            for (RegressionThreshold threshold : thresholds) {
                Double delta = deltas.get(threshold.metric());
                if (delta == null) reasons.add("comparison metric missing from baseline or candidate: " + threshold.metric());
                else if (delta < -threshold.maximumRegression())
                    reasons.add(threshold.metric() + " regression exceeds tolerance: " + delta);
            }
            return new Comparison(baseline, candidate, deltas, passDelta, !reasons.isEmpty(), reasons);
        }
    }
}
