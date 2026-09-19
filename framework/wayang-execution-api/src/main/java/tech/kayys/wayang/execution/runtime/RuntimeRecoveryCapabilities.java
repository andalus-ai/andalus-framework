package tech.kayys.wayang.execution.runtime;

public record RuntimeRecoveryCapabilities(
        boolean checkpoint,
        boolean pause,
        boolean resume,
        boolean migration,
        boolean restore
) {
    public static RuntimeRecoveryCapabilities full() {
        return new RuntimeRecoveryCapabilities(true, true, true, true, true);
    }

    public static RuntimeRecoveryCapabilities basic() {
        return new RuntimeRecoveryCapabilities(true, true, true, false, false);
    }
}
