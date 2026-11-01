package tech.kayys.andalus.harness.evaluation;

import java.util.concurrent.CompletionStage;

/** Pluggable evaluator for one evaluation case. */
@FunctionalInterface
public interface Evaluator {
    CompletionStage<EvaluationCaseResult> evaluate(EvaluationCase evaluationCase);
}
