package tech.kayys.wayang.execution.adaptation;

/**
 * Statistical or heuristic confidence score between 0.0 and 1.0.
 */
public record SignalConfidence(double value) {

    public SignalConfidence {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
    }

    public static SignalConfidence certain() {
        return new SignalConfidence(1.0);
    }

    public static SignalConfidence of(double value) {
        return new SignalConfidence(Math.max(0.0, Math.min(1.0, value)));
    }
}
