package tech.kayys.andalus.harness.evaluation;

import java.time.Instant;
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
    record Request(String evaluationId, String candidateId, String candidateVersion, Dataset dataset) {
        public Request {
            if (evaluationId == null || evaluationId.isBlank() || candidateId == null || candidateId.isBlank()
                    || candidateVersion == null || candidateVersion.isBlank() || dataset == null)
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
                  List<CaseResult> results, Map<String,Double> aggregateMetrics) {
        public Report {
            if (evaluationId == null || candidateId == null || candidateVersion == null || datasetId == null || datasetVersion == null)
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
                        request.dataset().id(), request.dataset().version(), started, Instant.now(), results, means);
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
}
