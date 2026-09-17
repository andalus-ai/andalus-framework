package tech.kayys.wayang.harness.execution.checkpoint;

public interface CheckpointPolicy {

    boolean shouldCheckpoint(CheckpointContext context);

    static CheckpointPolicy standard() {
        return context -> {
            String trigger = context.trigger();
            return "phase_completed".equalsIgnoreCase(trigger)
                    || "suspension".equalsIgnoreCase(trigger)
                    || "action_completed".equalsIgnoreCase(trigger)
                    || "shutdown".equalsIgnoreCase(trigger)
                    || "manual".equalsIgnoreCase(trigger);
        };
    }
}
