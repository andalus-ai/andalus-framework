package tech.kayys.andalus.harness.reasoning;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the structural integrity of a {@link ReasoningAssessment}
 * before it is returned by the {@link ReasoningRuntime}.
 *
 * <p>The validator does <em>not</em> evaluate the quality or correctness
 * of the reasoning; it only checks that the structural contract is met:
 * a non-blank conclusion, a non-null purpose, and a confidence value
 * within the valid range.</p>
 */
public class ReasoningResultValidator {

    /**
     * Validates the given assessment.
     *
     * @param assessment the assessment to validate
     * @return a list of error messages; empty means the assessment is structurally valid
     */
    public List<String> validate(ReasoningAssessment assessment) {
        List<String> errors = new ArrayList<>();

        if (assessment == null) {
            errors.add("ReasoningAssessment must not be null.");
            return errors;
        }

        if (assessment.conclusion() == null || assessment.conclusion().isBlank()) {
            errors.add("ReasoningAssessment must have a non-blank conclusion.");
        }

        if (assessment.purpose() == null) {
            errors.add("ReasoningAssessment must have a non-null purpose.");
        }

        if (assessment.confidence() < 0.0 || assessment.confidence() > 1.0) {
            errors.add("ReasoningAssessment confidence must be in [0.0, 1.0]; got: " + assessment.confidence());
        }

        if (assessment.id() == null || assessment.id().isBlank()) {
            errors.add("ReasoningAssessment must have a non-blank id.");
        }

        return errors;
    }

    /** Returns true if the assessment passes all structural checks. */
    public boolean isValid(ReasoningAssessment assessment) {
        return validate(assessment).isEmpty();
    }
}
